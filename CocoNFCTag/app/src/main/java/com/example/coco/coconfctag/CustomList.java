package com.example.coco.coconfctag;

/**
 * Created by meghanthan on 1/31/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList  extends ArrayAdapter<String> {
    private String[] names;
    private String[] desc;
    private Integer[] imageid;
    private String[] price;
    private Activity context;

    public CustomList(Activity context, String[] names, String[] desc,String[] price) {
        super(context, R.layout.list_layout, names);
        this.context = context;
        this.names = names;
        this.desc = desc;
        this.price = price;
        //this.imageid = imageid;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewDesc = (TextView) listViewItem.findViewById(R.id.textViewDesc);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);

        //ImageView image = (ImageView) listViewItem.findViewById(R.id.imageView);

        textViewName.setText(names[position]);
        textViewDesc.setText(desc[position]);
        textViewPrice.setText(price[position]);
        //image.setImageResource(imageid[position]);
        int t=0;
        for(int i=0;i<price.length;i++)
        {
            //t+=Intege
        }
        if(NfcMultiReader.tv_total!=null)
        {
            NfcMultiReader.tv_total.setText("Total : "+"vals");
        }
        return listViewItem;
    }
}