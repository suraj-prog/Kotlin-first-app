package com.example.mvvmcrudkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmcrudkotlin.databinding.ActivityMainBinding
import com.example.mvvmcrudkotlin.db.Subscriber
import com.example.mvvmcrudkotlin.db.SubscriberDAO
import com.example.mvvmcrudkotlin.db.SubscriberDatabase
import com.example.mvvmcrudkotlin.db.SubscriberRepository
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val dao : SubscriberDAO = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this,factory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this
        displaySubscribersList()
        initRecyclerView()
        subscriberViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this,it,Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecyclerView(){
        binding.SubscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter ({ selectedItem:Subscriber->listItemClicked(selectedItem) })
        binding.SubscriberRecyclerView.adapter = adapter
        displaySubscribersList()
    }
    private fun displaySubscribersList(){
         subscriberViewModel.subscribers.observe(this, Observer {
             Log.i("MYTAG",it.toString())
             adapter.setList(it)
             adapter.notifyDataSetChanged()
         })
    }
    private fun listItemClicked(subscriber: Subscriber){
       // Toast.makeText(this,"selected name is ${subscriber.name}",Toast.LENGTH_LONG).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}