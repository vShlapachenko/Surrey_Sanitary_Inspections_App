package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Violation;

public class ViolationListAdapter extends RecyclerView.Adapter<ViolationListAdapter.ViewHolder> {

    private Context context;
    private Inspection restaurantInspection;

    private int TYPE_HEAD = 0;
    private int TYPE_LIST = 1;

    public ViolationListAdapter(Context context, Inspection restaurantInspection) {
        this.context = context;
        this.restaurantInspection = restaurantInspection;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder holder;
        if (viewType == TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.violation_item_list, parent, false);
            holder = new ViewHolder(view, viewType);
            fillViolationList();
            return holder;
        } else if (viewType == TYPE_HEAD) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.violation_head_layout, parent, false);
            holder = new ViewHolder(view, viewType);
            fillViolationList();
            return holder;
        }
        throw new IllegalAccessError("Skipped return statements in onCreteViewHolder in " +
                "ViolationListAdapter");
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.view_type == TYPE_LIST) {
            Violation violation = restaurantInspection.getViolationList().get(position - 1);
            holder.violationImage.setImageResource(violation.getIconId());
            String setString = getSummaryString(violation);
            holder.summary.setText(setString);
            holder.hazardImage.setImageResource(violation.getHazIconId());

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, violation.getProblemDescription(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String getSummaryString(Violation violation) {
        String viol = "violation";
        int id = context.getResources().getIdentifier(viol + violation.getViolationNum(),
                "string", context.getPackageName());
        return context.getString(id);
    }

    private void fillViolationList() {
        for (Violation cur : restaurantInspection.getViolationList()) {
            int maxPermitViolationNum = 200;
            int maxTemperatureViolationNum = 300;
            int maxEquipmentViolationNum = 401;
            if (cur.getViolationNum() < maxPermitViolationNum) {
                cur.setIconId(R.drawable.permit);
            } else if (cur.getViolationNum() > maxPermitViolationNum
                    && cur.getViolationNum() < maxTemperatureViolationNum) {
                cur.setIconId(R.drawable.thermometer);
            } else if (cur.getViolationNum() > maxTemperatureViolationNum
                    && cur.getViolationNum() <= maxEquipmentViolationNum
                    && !isPestViolation(cur)) {
                cur.setIconId(R.drawable.equipment);
            } else if (isSanitaryViolation(cur)) {
                cur.setIconId(R.drawable.handwash);
            } else if (isPestViolation(cur)) {
                cur.setIconId(R.drawable.pest);
            } else {
                cur.setIconId(R.drawable.permit);
            }
        }
        for (Violation cur : restaurantInspection.getViolationList()) {
            if (cur.isCritical()) {
                cur.setHazIconId(R.drawable.high_hazard);
            } else {
                cur.setHazIconId(R.drawable.low_hazard);
            }
        }
    }

    private boolean isPestViolation(Violation v) {
        return v.getViolationNum() == 304 || v.getViolationNum() == 313;
    }

    private boolean isSanitaryViolation(Violation v) {
        return v.getViolationNum() == 402 || v.getViolationNum() == 403 || v.getViolationNum() == 404;
    }

    @Override
    public int getItemCount() {
        return restaurantInspection.getViolationList().size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int view_type;
        ImageView violationImage;
        TextView summary;
        ImageView hazardImage;
        RelativeLayout parentLayout;

        TextView violationHeader, summaryHeader, hazardHeader;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_LIST) {
                view_type = TYPE_LIST;
                violationImage = itemView.findViewById(R.id.item_violation_image);
                summary = itemView.findViewById(R.id.summary);
                hazardImage = itemView.findViewById(R.id.item_hazard);
                parentLayout = itemView.findViewById(R.id.parent_layout_violation);
            } else if (viewType == TYPE_HEAD) {
                violationHeader = itemView.findViewById(R.id.violation_header);
                summaryHeader = itemView.findViewById(R.id.summary_header);
                hazardHeader = itemView.findViewById(R.id.hazard_header);
                view_type = TYPE_HEAD;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_LIST;
        }
    }
}
