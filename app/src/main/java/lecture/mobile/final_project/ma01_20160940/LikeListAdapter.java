package lecture.mobile.final_project.ma01_20160940;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LikeListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<LikeListDto> myDataList;
    private LayoutInflater layoutInflater;

    public LikeListAdapter(Context context, int layout, ArrayList<LikeListDto> myDataList)
    {
        this.context = context;
        this.layout = layout;
        this.myDataList = myDataList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myDataList.size();
    }

    @Override
    public Object getItem(int pos) {
        return myDataList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return myDataList.get(pos).get_id();
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        final int position = pos;

        if(view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);
        }

        TextView textTitle = (TextView)view.findViewById(R.id.tvTitle);
        TextView textAddress = (TextView)view.findViewById(R.id.tvAddress);
        TextView textTel = (TextView)view.findViewById(R.id.tvTel);

        textTitle.setText(myDataList.get(pos).getTitle());
        textTel.setText(myDataList.get(pos).getNumber());
        textAddress.setText(myDataList.get(pos).getAddress());

        return view;
    }
    public void setList(ArrayList<LikeListDto> myDataList) {
        this.myDataList = myDataList;
    }
}

