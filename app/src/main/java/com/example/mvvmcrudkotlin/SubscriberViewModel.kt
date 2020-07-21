package com.example.mvvmcrudkotlin

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmcrudkotlin.db.Subscriber
import com.example.mvvmcrudkotlin.db.SubscriberRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(),Observable {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete : Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
      get() = statusMessage
     init{
        saveOrUpdateButtonText.value = "Save"
        clearOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate(){
        if(inputName.value == null){
            statusMessage.value = Event("Please enter Subscriber's name")
        }else  if(inputEmail.value == null){
            statusMessage.value = Event("Please enter Subscriber's Email")
        }else  if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()){
            statusMessage.value = Event("Please enter correct Subscriber's email")
        }else{
            if(isUpdateOrDelete){
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            }else {
                val name: String = inputName.value!!
                val email: String = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }
    fun clearOrDelete(){
        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else {
            clearAll()
        }
    }

    fun insert(subscriber: Subscriber) : Job = viewModelScope.launch {
        val newRowId:Long =  repository.insert(subscriber)
        if(newRowId>-1){
            statusMessage.value = Event("Subscriber Inserted Successfully $newRowId")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
        }

    fun update(subscriber: Subscriber) : Job = viewModelScope.launch {
        val noOfRows = repository.update(subscriber)
        if(noOfRows>0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRows Subscriber Updated Successfully")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun delete(subscriber: Subscriber) : Job = viewModelScope.launch {
        val noofRomsDelete = repository.delete(subscriber)
        if(noofRomsDelete>0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noofRomsDelete Subscriber Deleted Successfully")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun clearAll() : Job = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if(noOfRowsDeleted>0) {
            statusMessage.value = Event("$noOfRowsDeleted Subscriber Cleared Successfully")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber){
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearOrDeleteButtonText.value = "Delete"
    }
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}