/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.databinding.FragmentEggTimerBinding
import com.google.firebase.messaging.FirebaseMessaging

class EggTimerFragment : Fragment() {

    private val TOPIC = "breakfast"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentEggTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false)

        val viewModel = ViewModelProviders.of(this).get(EggTimerViewModel::class.java)
        binding.eggTimerViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        //Sending request for channel egg notifications by providing id and name
        createEggChannel()
        //Sending request for channel breakfast notifications by providing id and name
        createBreakfastChannel()
        //subscribe topic to get relevant notifications
        subscribeTopic()

        return binding.root
    }

    private fun createChannel(channelId: String, channelName: String) {
        // checking SDK version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            customizingNotificationChannel(notificationChannel)

            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        fun newInstance() = EggTimerFragment()
    }

    @SuppressLint("NewApi")
    private fun customizingNotificationChannel(channelObj:NotificationChannel){
        channelObj.apply {
            this.setShowBadge(false)
            this.enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = getString(R.string.breakfast_notification_channel_description)
        }
    }

    private fun createEggChannel() {
        createChannel(
            getString(R.string.egg_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )
    }

    private fun createBreakfastChannel() {
        createChannel(
            getString(R.string.breakfast_notification_channel_id),
            getString(R.string.breakfast_notification_channel_name)
        )
    }

    //Subscribe to a function to get notifications
    private fun subscribeTopic() {
        //Subscribe function returns a task which is checked for status
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var msg: String
                if (!task.isSuccessful) {
                    msg = getString(R.string.message_subscribe_failed)
                } else {
                    msg = getString(R.string.message_subscribed)
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }
}

