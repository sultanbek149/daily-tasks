package com.andruid.magic.dailytasks.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.andruid.magic.dailytasks.R
import com.andruid.magic.dailytasks.data.CATEGORY_PERSONAL
import com.andruid.magic.dailytasks.data.CATEGORY_WORK
import com.andruid.magic.dailytasks.data.STATUS_PENDING
import com.andruid.magic.dailytasks.database.Task
import com.andruid.magic.dailytasks.database.TaskRepository
import com.andruid.magic.dailytasks.databinding.ActivityAddTaskBinding
import com.andruid.magic.dailytasks.ui.viewbinding.viewBinding
import com.andruid.magic.dailytasks.util.getTaskTimeFromPicker
import com.andruid.magic.dailytasks.util.setShadow
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch

class AddTaskActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityAddTaskBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() {
        binding.categortRadioGroup.setOnCheckedChangeListener { radioGroup, id ->
            findViewById<MaterialRadioButton>(id).setShadow(R.color.scooter, R.dimen.shadow_radius, R.dimen.shadow_elevation, Gravity.BOTTOM, R.color.dodger_blue)
        }

        binding.addTasksBtn.setOnClickListener {
            val title = binding.taskNameET.text.toString().trim()
            if (title.isBlank()) {
                binding.taskNameInput.error = "Please enter task name"
                return@setOnClickListener
            }

            val hour = binding.timePickerET.selectedHour
            val minutes = binding.timePickerET.selectedMinute

            val taskMillis = getTaskTimeFromPicker(hour, minutes)
            Log.d("msLog", "selected time = $taskMillis")

            val task = Task(
                title = title,
                repeat = binding.repeatSwitch.isChecked,
                time = taskMillis,
                category = getCategoryFromRadioGroup(),
                status = STATUS_PENDING
            )

            lifecycleScope.launch {
                TaskRepository.insertTask(task)
                finish()
            }
        }
    }

    private fun getCategoryFromRadioGroup(): String {
        return when (binding.categortRadioGroup.checkedRadioButtonId) {
            R.id.work_radio_btn -> CATEGORY_WORK
            else -> CATEGORY_PERSONAL
        }
    }
}