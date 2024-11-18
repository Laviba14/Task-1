package com.example.taskorganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskExpandableListAdapter.TaskRemoveListener {

    private TaskExpandableListAdapter adapter;
    private List<String> priorityList;
    private HashMap<String, List<Task>> tasksByPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        priorityList = new ArrayList<>();
        tasksByPriority = new HashMap<>();

        // Initializing default tasks
        List<Task> highPriorityTasks = new ArrayList<>();
        highPriorityTasks.add(new Task("Task 1", "2024-11-20", "High"));
        tasksByPriority.put("High Priority", highPriorityTasks);
        priorityList.add("High Priority");

        List<Task> lowPriorityTasks = new ArrayList<>();
        lowPriorityTasks.add(new Task("Task 2", "2024-12-01", "Low"));
        tasksByPriority.put("Low Priority", lowPriorityTasks);
        priorityList.add("Low Priority");

        adapter = new TaskExpandableListAdapter(this, priorityList, tasksByPriority, this);
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);

        // Handle Add Task button click
        ImageButton addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText taskTitleEditText = dialogView.findViewById(R.id.taskTitleEditText);
        EditText taskDeadlineEditText = dialogView.findViewById(R.id.taskDeadlineEditText);
        Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);

        // Set up priority spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.priority_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskTitle = taskTitleEditText.getText().toString();
            String taskDeadline = taskDeadlineEditText.getText().toString();
            String taskPriority = prioritySpinner.getSelectedItem().toString();

            if (!taskTitle.isEmpty() && !taskDeadline.isEmpty()) {
                addNewTask(taskTitle, taskDeadline, taskPriority);
            } else {
                Toast.makeText(MainActivity.this, "Please enter both title and deadline.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addNewTask(String taskTitle, String taskDeadline, String taskPriority) {
        Task newTask = new Task(taskTitle, taskDeadline, taskPriority);

        List<Task> tasks = tasksByPriority.get(taskPriority + " Priority");
        if (tasks == null) {
            tasks = new ArrayList<>();
            tasksByPriority.put(taskPriority + " Priority", tasks);
            priorityList.add(taskPriority + " Priority");
        }

        tasks.add(newTask);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskRemoved(int groupPosition, int childPosition) {
        String group = priorityList.get(groupPosition);
        List<Task> tasks = tasksByPriority.get(group);

        if (tasks != null && childPosition >= 0 && childPosition < tasks.size()) {
            tasks.remove(childPosition);

            if (tasks.isEmpty()) {
                tasksByPriority.remove(group);
                priorityList.remove(groupPosition);
            }

            adapter.notifyDataSetChanged();
        }
    }
}
