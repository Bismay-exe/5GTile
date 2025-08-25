package com.example.fivegtile

import java.io.BufferedReader
import java.io.InputStreamReader

object RootShell {
    data class Result(val code: Int, val out: String, val err: String)

    fun run(vararg command: String): Result {
        val joined = command.joinToString(" && ")
        return runRaw("su", "-c", joined)
    }

    fun runRaw(vararg args: String): Result {
        return try {
            val pb = ProcessBuilder(*args)
            pb.redirectErrorStream(false)
            val proc = pb.start()
            val out = BufferedReader(InputStreamReader(proc.inputStream)).readText()
            val err = BufferedReader(InputStreamReader(proc.errorStream)).readText()
            val code = proc.waitFor()
            Result(code, out.trim(), err.trim())
        } catch (e: Exception) {
            Result(-1, "", e.message ?: "error")
        }
    }
}
