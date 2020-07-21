package com.example.mvvmcrudkotlin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmcrudkotlin.databinding.ListItemBinding
import com.example.mvvmcrudkotlin.db.Subscriber
import com.example.mvvmcrudkotlin.generated.callback.OnClickListener

class MyRecyclerViewAdapter(private val clickListener: (Subscriber)->Unit) : RecyclerView.Adapter<MyViewHolder>()
{
    private val subscribersList =  ArrayList<Subscriber>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater:LayoutInflater = LayoutInflater.from(parent.context)
        val binding : ListItemBinding = DataBindingUtil.inflate(layoutInflater,R.layout.list_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscribersList[position],clickListener)
    }
    fun setList(subscriber: List<Subscriber>){
        subscribersList.clear()
        subscribersList.addAll(subscriber)
    }

}
class MyViewHolder(val binding: ListItemBinding):RecyclerView.ViewHolder(binding.root){
    fun bind (subscriber: Subscriber,clickListener: (Subscriber)->Unit){
        binding.nameTextView.text = subscriber.name
        binding.emailTextView.text = subscriber.email
        binding.listItemLayout.setOnClickListener{
            clickListener(subscriber)
        }
    }
}