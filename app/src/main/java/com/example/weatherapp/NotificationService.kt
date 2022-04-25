package com.example.weatherapp

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class NotificationService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    var timer : CountDownTimer?=null
    var intent = Intent("end")

    override fun onCreate() {
        //30 minute intervals
        timer = object: CountDownTimer(1800000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                sendBroadcast(intent)

                //reset timer
                timer.let {
                    cancel()
                    start()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer?.start()
        return super.onStartCommand(intent, flags, startId)
    }
}