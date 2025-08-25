package bismay.exe.fivegtile

import android.content.Context
import android.telephony.SubscriptionManager

object TelephonyToggles {

    private const val RAF_NR  = 1 shl 14

    fun getActiveDataSubId(context: Context): Int {
        val sm = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        return try {
            SubscriptionManager.getActiveDataSubscriptionId()
        } catch (_: Throwable) {
            sm.activeSubscriptionInfoList?.firstOrNull()?.subscriptionId ?: -1
        }
    }

    fun isNrAllowed(subId: Int): Boolean {
        val getAllowed = RootShell.run("cmd phone get-allowed-network-types-for-reason $subId user")
        if (getAllowed.code == 0 && getAllowed.out.isNotBlank()) {
            val mask = parseMask(getAllowed.out)
            if (mask != null) return (mask and RAF_NR) != 0
        }
        return false
    }

    fun toggleNr(subId: Int): Boolean {
        val getAllowed = RootShell.run("cmd phone get-allowed-network-types-for-reason $subId user")
        if (getAllowed.code == 0 && getAllowed.out.isNotBlank()) {
            val curMask = parseMask(getAllowed.out) ?: return false
            val newMask = if ((curMask and RAF_NR) != 0) (curMask and RAF_NR.inv()) else (curMask or RAF_NR)
            val setRes = RootShell.run("cmd phone set-allowed-network-types-for-reason $subId user $newMask")
            return setRes.code == 0
        }
        return false
    }

    private fun parseMask(out: String): Int? {
        // FIXED regex: escaped backslashes
        val regex = Regex("\\b(\\d{2,})\\b")
        val ints = regex.findAll(out).map { it.value.toIntOrNull() }.filterNotNull().toList()
        return if (ints.isNotEmpty()) ints.last() else null
    }
}
