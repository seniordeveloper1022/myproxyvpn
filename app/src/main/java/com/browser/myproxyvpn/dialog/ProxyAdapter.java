package com.browser.myproxyvpn.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.browser.myproxyvpn.R;

public class ProxyAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ProxyModel> proxies;
    private int selectedItem = -1;
//    SharedPreferences pref;
//    String selectedCountry;

    public ProxyAdapter(Context context, ArrayList<ProxyModel> proxies) {
        this.context = context;
        this.proxies = proxies;
//        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        if (pref.contains(SELECTED_FLAG_ID)) {
//            selectedCountry = pref.getString(SELECTED_FLAG_ID, "");
//        }
    }

    @Override
    public int getCount() {
        return this.proxies.size();
    }

    @Override
    public ProxyModel getItem(int position) {
        return proxies.get(position);
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (View) inflater.inflate(R.layout.proxy_item, null);
            holder = new ViewHolder();
            holder.flag = convertView.findViewById(R.id.country_flag);
            holder.name = convertView.findViewById(R.id.country_name);
            holder.unlock = convertView.findViewById(R.id.premium_extra);
            holder.timer = convertView.findViewById(R.id.timer);
           /* if(proxies.get(position).getType().equals("Premium"))
                holder.unlock.setVisibility(View.VISIBLE);
            else
                holder.unlock.setVisibility(View.GONE);
            holder.timer.setVisibility(View.GONE);
*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (proxies.get(position).getType().equals("Premium")) {

            if (position == selectedItem) {
                // customize the selected item's background and sub views

                holder.unlock.setVisibility(View.GONE);
                holder.timer.setVisibility(View.GONE);
                 convertView.setBackgroundColor(context.getResources().getColor(R.color.secondary_color_settings));
            } else {
                holder.unlock.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(context.getResources().getColor(R.color.transparent));

                // holder.name.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            }


//            String country = proxies.get(position).getCountry();
////            Log.e("SELECTED COUNTRY ----?", selectedCountry);
//            if (selectedCountry != null && selectedCountry.equals(country)) {
//                convertView.setBackgroundColor(context.getResources().getColor(R.color.secondary_color_settings));
//            }


        } else {
            holder.unlock.setVisibility(View.GONE);

           // convertView.setBackgroundColor(context.getResources().getColor(R.color.secondary_color_settings));

        }

        try {
            // get input stream
            InputStream ims = context.getAssets().open("flags/" + proxies.get(position).getCountry() + ".png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            holder.flag.setImageDrawable(d);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        holder.name.setText(proxies.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        public ImageView flag;
        public TextView name;
        public LinearLayout unlock;
        public LinearLayout timer;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

}
