package com.example.rss.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rss.R;
import com.example.rss.model.Site;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by paco on 6/02/18.
 */

public class SitesAdapter extends RecyclerView.Adapter<SitesAdapter.ViewHolder> {
    private ArrayList<Site> sites;

    public SitesAdapter(){
        this.sites = new ArrayList<>();
    }

    public void setRepos(ArrayList<Site> repos) {
        sites = repos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView1) TextView name;
        @BindView(R.id.textView2) TextView link;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View siteView = inflater.inflate(R.layout.item_view, parent, false);

        // Return a new holder instance
        return new ViewHolder(siteView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Site site = sites.get(position);

        holder.name.setText(site.getName());
        holder.link.setText(site.getLink());
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    public int getId(int position){

        return this.sites.get(position).getId();
    }

    public Site getAt(int position){
        Site site;
        site = this.sites.get(position);
        return  site;
    }

    public void add(Site site) {
        this.sites.add(site);
        notifyItemInserted(sites.size() - 1);
        notifyItemRangeChanged(0, sites.size() - 1);
    }

    public void modifyAt(Site site, int position) {
        this.sites.set(position, site);
        notifyItemChanged(position);
    }

    public void removeAt(int position) {
        this.sites.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, sites.size() - 1);
    }
}
