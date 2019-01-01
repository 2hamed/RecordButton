package com.hmomeni.customviewtutorial

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recordButton.mode = RecordButton.Mode.Ready

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            stopLatch.set(true)
            when (checkedId) {
                R.id.idle -> recordButton.mode = RecordButton.Mode.Idle
                R.id.ready -> recordButton.mode = RecordButton.Mode.Ready
                R.id.recording -> recordButton.mode = RecordButton.Mode.Recording
                R.id.loading -> {
                    stopLatch.set(false)
                    recordButton.mode = RecordButton.Mode.Loading
                    recordButton.progress = 0
                    var step = 0
                    timer(100, TimeUnit.MILLISECONDS) {
                        recordButton.progress = ++step
                        if (step >= 100) {
                            it.set(true)
                        }
                    }
                }
            }
        }
    }

    private val handler = Handler()
    private val stopLatch = AtomicBoolean()
    private fun timer(interval: Long, timeUnit: TimeUnit, task: (AtomicBoolean) -> Unit): AtomicBoolean {
        handler.postDelayed({
            if (stopLatch.get()) {
                return@postDelayed
            }
            task.invoke(stopLatch)
            timer(interval, timeUnit, task)
        }, timeUnit.toMillis(interval))
        return stopLatch
    }
}
