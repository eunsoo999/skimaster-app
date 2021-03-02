package lecture.mobile.final_project.ma01_20160940;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

//메인액티비티에서 출력되는 리스트뷰의 커스텀어댑터
public class MyCustomAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<SkiItem> myDataList;
    private LayoutInflater layoutInflater;

    public MyCustomAdapter(Context context, int layout, ArrayList<SkiItem> myDataList)
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
        textTel.setText(myDataList.get(pos).getTel());
        textAddress.setText(myDataList.get(pos).getAddress());

        return view;
    }
    public void setList(ArrayList<SkiItem> myDataList) {
        this.myDataList = myDataList;
    }
}

