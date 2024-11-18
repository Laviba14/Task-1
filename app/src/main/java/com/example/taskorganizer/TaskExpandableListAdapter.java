package com.example.taskorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class TaskExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> priorityList;
    private HashMap<String, List<Task>> tasksByPriority;
    private TaskRemoveListener taskRemoveListener;

    public TaskExpandableListAdapter(Context context, List<String> priorityList, HashMap<String, List<Task>> tasksByPriority, TaskRemoveListener taskRemoveListener) {
        this.context = context;
        this.priorityList = priorityList;
        this.tasksByPriority = tasksByPriority;
        this.taskRemoveListener = taskRemoveListener;
    }

    @Override
    public int getGroupCount() {
        return priorityList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return tasksByPriority.get(priorityList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return priorityList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return tasksByPriority.get(priorityList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_item, null);
        }
        TextView groupTitleTextView = convertView.findViewById(R.id.groupTitleText);
        groupTitleTextView.setText(groupTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Task task = (Task) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_item, null);
        }
        TextView taskTitleTextView = convertView.findViewById(R.id.taskTitleText);
        TextView taskDeadlineTextView = convertView.findViewById(R.id.taskDeadlineText);
        ImageButton removeTaskButton = convertView.findViewById(R.id.removeTaskButton);

        taskTitleTextView.setText(task.getTitle());
        taskDeadlineTextView.setText(task.getDeadline());
        removeTaskButton.setOnClickListener(v -> taskRemoveListener.onTaskRemoved(groupPosition, childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface TaskRemoveListener {
        void onTaskRemoved(int groupPosition, int childPosition);
    }
}
