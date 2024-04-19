package com.bangbet.brvex

import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class BindingHolderDispatcher(val adapter: BindingAdapter) {

    init {
        adapter.onCreate {viewType->
            tag = ViewHolderHelper(itemView)
            typeLayoutMap.forEach {
                if (typeLayoutMap[it.key]?.contains(viewType) == true){
                    viewHolderContextMap[it.key]?.onCreateViewHolder?.invoke(this,viewType)
                }
            }
        }
        adapter.onBind {
            typeLayoutMap.forEach {
                if (typeLayoutMap[it.key]?.contains(bindingItemViewType) == true){
                    viewHolderContextMap[it.key]?.onBindViewHolder?.invoke(this)
                }
            }
        }
        adapter.onPayload { payload->
            typeLayoutMap.forEach {
                if (typeLayoutMap[it.key]?.contains(bindingItemViewType) == true){
                    viewHolderContextMap[it.key]?.onBindViewHolderPayload?.invoke(this,payload)
                }
            }
        }
        adapter.onAttach {
            typeLayoutMap.forEach {
                if (typeLayoutMap[it.key]?.contains(bindingItemViewType) == true){
                    viewHolderContextMap[it.key]?.onAttachViewHolder?.invoke(this)
                }
            }
        }
        adapter.onDetach{
            typeLayoutMap.forEach {
                if (typeLayoutMap[it.key]?.contains(bindingItemViewType) == true){
                    viewHolderContextMap[it.key]?.onDetachViewHolder?.invoke(this)
                }
            }
        }
        adapter.onRecycle {
            typeLayoutMap.forEach {
                if (typeLayoutMap[it.key]?.contains(bindingItemViewType) == true){
                    viewHolderContextMap[it.key]?.onRecycleViewHolder?.invoke(this)
                }
            }
        }
    }

    val viewHolderContextMap = HashMap<KType, ViewHolderContext>()

    val typeLayoutMap = HashMap<KType,List<Int>>()

    inline fun <reified M> join(@LayoutRes layoutId: Int, viewHolderContext: ViewHolderContext): BindingHolderDispatcher {
        typeLayoutMap[typeOf<M>()] = arrayListOf(layoutId)
        viewHolderContextMap[typeOf<M>()] = viewHolderContext
        adapter.addType<M>(layoutId)
        return this
    }

    inline fun <reified M> join(allLayout: List<Int>, noinline block: M.(position: Int) -> Int, viewHolderContext: ViewHolderContext): BindingHolderDispatcher {
        typeLayoutMap[typeOf<M>()] = allLayout
        viewHolderContextMap[typeOf<M>()] = viewHolderContext
        adapter.addType(block)
        return this
    }

}

open class ViewHolderContext{

    var onCreateViewHolder: (BindingAdapter.BindingViewHolder.(viewType: Int) -> Unit)? = null
    var onBindViewHolder: (BindingAdapter.BindingViewHolder.() -> Unit)? = null
    var onBindViewHolderPayload: (BindingAdapter.BindingViewHolder.(payloads: MutableList<Any>) -> Unit)? = null
    var onAttachViewHolder: (BindingAdapter.BindingViewHolder.() -> Unit)? = null
    var onDetachViewHolder: (BindingAdapter.BindingViewHolder.() -> Unit)? = null
    var onRecycleViewHolder: (BindingAdapter.BindingViewHolder.() -> Unit)? = null

    open fun onCreate(block: (BindingAdapter.BindingViewHolder.(viewType: Int) -> Unit)){
        onCreateViewHolder = block
    }

    open fun onBind(block: (BindingAdapter.BindingViewHolder.() -> Unit)){
        onBindViewHolder = block
    }

    open fun onPayload(block: (BindingAdapter.BindingViewHolder.(payloads: MutableList<Any>) -> Unit)){
        onBindViewHolderPayload = block
    }

    open fun onAttach(block: (BindingAdapter.BindingViewHolder.() -> Unit)){
        onAttachViewHolder = block
    }

    open fun onDetach(block: (BindingAdapter.BindingViewHolder.() -> Unit)){
        onDetachViewHolder = block
    }

    open fun onRecycle(block: (BindingAdapter.BindingViewHolder.() -> Unit)){
        onRecycleViewHolder = block
    }

}

class ViewHolderHelper(val itemView: View){

    private val array = HashMap<Int, View>()

    init {
        array.putAll(getAllHasIdView(itemView))
    }

    private fun getAllHasIdView(view: View):Map<Int,View>{
        val idViewMap = HashMap<Int,View>()
        if (view.id != View.NO_ID){
            idViewMap[view.id] = view
        }
        if (view is ViewGroup){
            for (index in 0 until view.childCount){
                idViewMap.putAll(getAllHasIdView(view.getChildAt(index)))
            }
        }
        return idViewMap;
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : View?> getViewById(viewId: Int): T?{
        var view = array[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            array[viewId] = view
        }
        return view as T
    }

    fun <T : View?> getView(viewId: Int): T? {
        return getViewById(viewId)
    }

    fun getImageView(viewId: Int): ImageView? {
        return getViewById<ImageView>(viewId)
    }

    fun getTextView(viewId: Int): TextView? {
        return getViewById<TextView>(viewId)
    }

    fun getViewGroup(viewId: Int): ViewGroup? {
        return getViewById<ViewGroup>(viewId)
    }

    fun getCompoundButton(viewId: Int): CompoundButton? {
        return getViewById<CompoundButton>(viewId)
    }

    fun getSpinner(viewId: Int): Spinner? {
        return getViewById<Spinner>(viewId)
    }

    fun getRecyclerView(viewId: Int): RecyclerView? {
        return getViewById<RecyclerView>(viewId)
    }
}

fun BindingAdapter.BindingViewHolder.getViewHolderHelper(): ViewHolderHelper{
    return tag as ViewHolderHelper
}