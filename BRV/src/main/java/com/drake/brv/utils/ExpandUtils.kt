package com.drake.brv.utils

import com.drake.brv.item.ItemExpand

//
fun getItemPosition(any: Any,list:List<Any>) : Int{
    var position = 0;
    for (i in list.indices){
        if (any == list[i]){
            break
        }
        if (any is ItemExpand && any.itemExpand){
            position += getItemAndChildCount(any)
        } else {
            position++
        }
    }
    return position
}

fun getItemAndChildCount(itemExpand: ItemExpand):Int{
    return 1 +
            if (itemExpand.getItemSublist().isEmpty()){
                0
            } else {
                itemExpand.getItemSublist().sumOf {
                    if (it is ItemExpand && it.itemExpand){
                        getItemAndChildCount(itemExpand)
                    } else {
                        1
                    }
                }
            }
}

fun getExpandItemList(itemExpand: ItemExpand):List<Any?>{
    return arrayListOf<Any?>(itemExpand)  +
            itemExpand.getItemSublist().flatMap{
                if (it is ItemExpand && it.itemExpand){
                    getExpandItemList(itemExpand)
                } else {
                    arrayListOf(it)
                }
            }
}

fun expandListToCollapseList(list: List<Any?>): MutableList<Any?>{
    val result = ArrayList<Any?>()
    for (any in list){
        val last = result.lastOrNull()
        if (last is ItemExpand && isNestedChild(last,any)){
            // 不用再递归了
        } else{
            result.add(any)
        }
    }
    return result
}

fun isNestedChild(itemExpand: ItemExpand,any: Any?): Boolean{
    return itemExpand.getItemSublist().contains(any) || itemExpand.getItemSublist().any {
        it is ItemExpand && isNestedChild(it,any)
    }
}

//fun getAllItemExpandModels(): List<Any?>?{
//    val all = ArrayList<Any?>()
//    for (item in expandListToCollapseList(mutable)){
//        if (item is ItemExpand){
//            all.addAll(getAllExpandItemList(item))
//        } else{
//            all.add(item)
//        }
//    }
//    return all;
//}

fun getAllExpandItemList(itemExpand: ItemExpand):List<Any?>{
    return arrayListOf<Any?>(itemExpand)  +
            itemExpand.getItemSublist().flatMap{
                if (it is ItemExpand){
                    getExpandItemList(itemExpand)
                } else {
                    arrayListOf(it)
                }
            }
}
