package com.gionee.autotest.field.ui.outgoing.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;

import java.util.ArrayList;

import static com.gionee.autotest.field.FieldApplication.context;


public class OutGoingReportAdapter extends BaseExpandableListAdapter {
    private ArrayList<ArrayList<OutGoingCallResult>> data = new ArrayList<>();

    public void updateData(ArrayList<ArrayList<OutGoingCallResult>> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).get(childPosition);
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
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder gHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.out_going_report_group_item, parent, false);
            gHolder = new GroupHolder(convertView);
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupHolder) convertView.getTag();
        }
        gHolder.cycleID.setText(String.format("第%1$s轮", groupPosition + 1));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder cHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.out_going_report_child_item, parent, false);
            convertView.setTag(cHolder = new ChildHolder(convertView));
        } else {
            cHolder = (ChildHolder) convertView.getTag();
        }
        OutGoingCallResult result = data.get(groupPosition).get(childPosition);
        cHolder.numberTV.setText(result.number + (result.isVerify ? "(复测)" : ""));
        cHolder.resultTV.setText(result.result ? "成功" : "失败");
        cHolder.root.setBackgroundResource(result.result ? R.drawable.item_color_green : R.drawable.item_color_red);
        cHolder.dialTimeTV.setText(result.dialTime);
        cHolder.offHookTimeTV.setText(result.offHookTime);
        cHolder.hangUpTimeTV.setText(result.hangUpTime);
        return convertView;
    }

    class GroupHolder {
        TextView cycleID;
        TextView timeTV;

        GroupHolder(View v) {
            cycleID = (TextView) v.findViewById(R.id.out_going_group_cycle_id);
            timeTV = (TextView) v.findViewById(R.id.out_going_group_timeTV);
        }
    }

    class ChildHolder {
        LinearLayout root;
        TextView numberTV;
        TextView resultTV;
        TextView dialTimeTV;
        TextView offHookTimeTV;
        TextView hangUpTimeTV;

        ChildHolder(View v) {
            root = (LinearLayout) v.findViewById(R.id.out_going_child_root);
            numberTV = (TextView) v.findViewById(R.id.out_going_child_numberTV);
            resultTV = (TextView) v.findViewById(R.id.out_going_child_resultTV);
            dialTimeTV = (TextView) v.findViewById(R.id.out_going_child_dialTimeTV);
            offHookTimeTV = (TextView) v.findViewById(R.id.out_going_child_offHookTimeTV);
            hangUpTimeTV = (TextView) v.findViewById(R.id.out_going_child_hangUpTimeTV);
        }
    }
}