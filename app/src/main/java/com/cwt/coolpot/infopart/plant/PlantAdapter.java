package com.cwt.coolpot.infopart.plant;

/**
 * 植物图片识别后展示结果RecyclerView的适配器
 * 后改为用ViewPager展示
 * Created by 曹吵吵 on 2017/10/24 0024.
 */

/*public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {
    private List<Plant.ResultBean> plantList;

    public PlantAdapter(List<Plant.ResultBean> plantList){
        this.plantList=plantList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plant_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Plant.ResultBean plant=plantList.get(position);
        holder.plantName.setText("名称："+dealStr(plant.Name));
        holder.plantLatinName.setText("拉丁名："+dealStr(plant.LatinName));
        holder.plantAliasName.setText("别名："+dealStr(plant.AliasName));
        holder.plantFamily.setText("科名："+dealStr(plant.Family));
        holder.plantGenus.setText("属名："+dealStr(plant.Genus));
        holder.plantScore.setText("可能性："+plant.Score+"%");
        String url=plant.ImageUrl;
        if (url=="")
            Glide.with(holder.view.getContext()).load(R.drawable.no_pic).into(holder.plantExamplePic);
        else
            Glide.with(holder.view.getContext()).load(url).into(holder.plantExamplePic);
    }

    private String dealStr(String info){
        if (info==""||info.isEmpty())
            return "暂无";
        return info;
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView plantName,plantLatinName,plantAliasName,plantFamily,plantGenus,plantScore;
        ImageView plantExamplePic;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            plantAliasName=(TextView)itemView.findViewById(R.id.plant_alias_name);
            plantFamily=(TextView)itemView.findViewById(R.id.plant_family);
            plantGenus=(TextView)itemView.findViewById(R.id.plant_genus);
            plantLatinName=(TextView)itemView.findViewById(R.id.plant_latin_name);
            plantName=(TextView)itemView.findViewById(R.id.plant_name);
            plantScore=(TextView)itemView.findViewById(R.id.plant_score);
            plantExamplePic=(ImageView)itemView.findViewById(R.id.plant_example_pic);
        }
    }
}*/
