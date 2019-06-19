package com.example.hi.album;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

// Custom list view trong AnhFragment
//***Show Ảnh theo thời gian***//
//public class CustomListviewImageAdapter extends ArrayAdapter<ArrayList<Hinh>> {
//
//    Context context;
//
//    //***Mảng lưu số lượng ảnh trong 1 ngày nào đó***//
////    ArrayList<Integer> mang;
//    ArrayList<ArrayList<Hinh>> mang;
//    //***Layout muốn dán***//
//    int layoutResource;
//
//    public CustomListviewImageAdapter(Context context, ArrayList<ArrayList<Hinh>> mang, int layoutResource) {
//        super(context, layoutResource, mang);
//        this.context = context;
//        this.mang = mang;
//        this.layoutResource = layoutResource;
//    }
//
//    @Override
//    public int getCount() {
//        return mang.size();
//    }
//
//    //***ViewHolder***//
//    public class ViewHolder {
//        TextView textView;
//        RecyclerView recyclerView;
//
//    }
//
//
//    @NonNull
//    @Override
//    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        long start = System.currentTimeMillis();
//
//        LayoutInflater inf = ((MainActivity) context).getLayoutInflater();
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = inf.inflate(layoutResource, null);
//            viewHolder.textView = convertView.findViewById(R.id.tvdate);
//            viewHolder.recyclerView = convertView.findViewById(R.id.recyclerView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        String date = mang.get(position).get(0).getAddDate().toString();
//        viewHolder.textView.setText(String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6, 8)));
//        CustomRecyclerviewAdapter customRecyclerviewAdapter = new CustomRecyclerviewAdapter(context, mang.get(position), false, position);
//        viewHolder.recyclerView.setAdapter(customRecyclerviewAdapter);
//        viewHolder.recyclerView.setHasFixedSize(true);
//        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 4);
//
//        viewHolder.recyclerView.setLayoutManager(mGridLayoutManager);
//
//        MainActivity.funcExecuteTime.put("getView CustomListviewImageAdapter", System.currentTimeMillis() - start);
//
//        return convertView;
//    }
//}
public class CustomListviewImageAdapter extends RecyclerView.Adapter<CustomListviewImageAdapter.MyViewHolder>{

        Context context;

    //***Mảng lưu số lượng ảnh trong 1 ngày nào đó***//
    ArrayList<ArrayList<Hinh>> mang;
    //***Layout muốn dán***//
    int layoutResource;

    public CustomListviewImageAdapter(Context context, ArrayList<ArrayList<Hinh>> mang, int layoutResource) {
        this.context = context;
        this.mang = mang;
        this.layoutResource = layoutResource;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((Activity)context).getLayoutInflater().inflate(layoutResource, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        long start = System.currentTimeMillis();
        String date = mang.get(position).get(0).getAddDate().toString();
        holder.textView.setText(String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6, 8)));
        CustomRecyclerviewAdapter customRecyclerviewAdapter = new CustomRecyclerviewAdapter(context, mang.get(position), false, position);
        holder.recyclerView.setAdapter(customRecyclerviewAdapter);
        holder.recyclerView.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 4);

        holder.recyclerView.setLayoutManager(mGridLayoutManager);

        MainActivity.funcExecuteTime.put("getView CustomListviewImageAdapter", System.currentTimeMillis() - start);
    }

    @Override
    public int getItemCount() {
        return mang.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tvdate);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }
}

