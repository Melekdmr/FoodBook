package com.melekdmr.foodbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melekdmr.foodbook.databinding.RecyclerRowBinding

class FoodAdapter(val foodList:ArrayList<Food>):RecyclerView.Adapter< FoodAdapter.FoodHolder>(){

    class FoodHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }
    /* Bu kod parçacığı, RecyclerView için gerekli olan Adapter sınıfının
    üç önemli fonksiyonunu tanımlar. onCreateViewHolder fonksiyonu,
    her bir görünüm öğesi için ViewHolder'ın oluşturulmasını sağlar. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FoodHolder(binding)
    }
   /* onBindViewHolder fonksiyonu, her bir öğe
   için veri bağlama işlemini gerçekleştirir. */
    override fun getItemCount(): Int {
        return foodList.size

    }
    /*  getItemCount fonksiyonu ise öğe sayısını belirtir.
    İlk olarak, holder.binding.recyclerviewText.text ifadesi kullanılarak,
    belirli bir pozisyondaki öğenin adı, FoodHolder sınıfında bulunan
    bir TextView'e atanıyor. Bu, öğenin adını gösterecek olan metni
    RecyclerView'ın ilgili öğesine yerleştirir.*/
    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.binding.recyclerviewText.text = foodList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,FoodActivity::class.java)
            /*   intent.putExtra("info","old") ifadesiyle, "info" adında bir ekstra veri
            ekleniyor ve değeri "old" olarak belirleniyor.
             intent.putExtra("id",foodList.get(position).id) ifadesiyle,
            "id" adında başka bir ekstra veri ekleniyor ve değeri, belirli
             bir pozisyondaki öğenin ID'si olarak atanıyor.
            Son olarak, holder.itemView.context.startActivity(intent) ifadesi,
             ayarlanan Intente göre FoodActivity'e geçişi sağlıyor.*/
            intent.putExtra("info","old")
            intent.putExtra("id",foodList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

}