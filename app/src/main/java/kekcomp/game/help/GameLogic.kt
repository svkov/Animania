package kekcomp.game.help

import kekcomp.game.R
import kekcomp.game.kt_activity.GameActivity
import kekcomp.game.utils.SetUtils
import java.util.*

/**
 * Created by svyatoslav on 26.01.18.
 */

class GameLogic(val activity: GameActivity) {

    val MODE_PRIVATE = 0
    val NUMBER_OF_ANIME = activity.NUMBER_OF_ANIME
    var seed = generateSeed()
        set(value) {
            winAnime = getAnimeName(value)
            field = value
        }
    val anime_names = activity.resources.getStringArray(R.array.anime_names)
    var winAnime = getAnimeName(seed)



    fun getAnimeNames(): Array<String> {
        val abc = generateABC()
        return unjumble(Array(4, { i -> if (i == 0) getAnimeName(seed) else getAnimeName(abc[i - 1])}))
    }

    fun getArrayOfCurrentAnime() : Array<String> {
        return anime_names[seed].split(" ").toTypedArray()
    }

    private fun getAnimeName(n : Int) : String {
        var str : String = anime_names[n].split(" ")[2]
        //var ch = str.toCharArray()
        //ch[0] = str[0].toTitleCase()
        //str = String(ch)
        println(str)
        return str.subSequence(0, str.lastIndex).replace(Regex("_"), " ")
    }
    private fun unjumble(arr : Array<String>) : Array<String> {
        val newArr : Array<String> = Array(arr.size, {""})
        val flags : Array<Boolean> = Array(arr.size, {false})
        for(item in arr){
            while(true){
                val r = Random().nextInt(arr.size)
                if(!flags[r]){
                    flags[r] = true
                    newArr[r] = item
                    break
                }
            }
        }
        return newArr
    }

    private fun generateSeed() : Int {
        val animeList : MutableMap<String, *> = activity.getSharedPreferences("solved_anime", MODE_PRIVATE).all
        val list : MutableList<Int> = ArrayList()
        for (key in animeList.keys){
            val value = animeList[key]
            if(value is Int) list.add(value)
        }
        val set : MutableSet<Int> = HashSet(list)
        println("Solved set: $set")
        val fullSet : MutableSet<Int> = mutableSetOf()
        repeat(NUMBER_OF_ANIME, {i -> fullSet.add(i)})
        val usableSet = SetUtils.complement(fullSet, set)
        if(usableSet.size == 0) {
            println("call newGame()")
            activity.newGame()
            return 0
        }
        return usableSet.toIntArray()[Random().nextInt(usableSet.size)]
    }

    private fun generateABC() : IntArray {
        val a : Int = Random().nextInt(NUMBER_OF_ANIME)
        val b : Int = (a * 10) % NUMBER_OF_ANIME
        val c : Int = (b * 10) % NUMBER_OF_ANIME
        return intArrayOf(a, b, c)
    }

}