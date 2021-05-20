package david.fernandez.marvel.presenter

import android.content.Context
import david.fernandez.marvel.R
import david.fernandez.marvel.data.SuperheroesInterface
import david.fernandez.marvel.model.SuperHeroe
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.CountDownLatch

class MarvelPresenter (var superheroesInterface: SuperheroesInterface){

    private lateinit var client:OkHttpClient
    private lateinit var listaHeroes:ArrayList<SuperHeroe>
    private lateinit var con:Context
    private var offset:Int=0



    fun getClient(): OkHttpClient {
        client=OkHttpClient()
        return client
    }

    fun setContext(context: Context){
        con=context
    }



    fun getSuperheroes(offset: Int) : List<SuperHeroe> {
        this.offset+=offset
        if(this.offset==0){
            listaHeroes=ArrayList<SuperHeroe>()
        }

        var timestamp=System.currentTimeMillis()
        var hashString=timestamp.toString()+con.getString(R.string.privatekey)+con.getString(R.string.apikey)
        var hash=md5(hashString)
        val request = Request.Builder()
            .url(con.getString(R.string.getheroesurl)+"?apikey="+con.getString(R.string.apikey)+"&ts="+timestamp+"&hash="+hash+"&offset="+offset+"&limit="+100)
            .build()
        var latch=CountDownLatch(1);
        val call = getClient().newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("onFailure")
                superheroesInterface.showError(con.getString(R.string.badrest))
                latch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code()==200){
                    var resp=response.body()?.string()
                    parseJson(resp)
                    latch.countDown()
                }else{
                    if(response.code()==400){
                        superheroesInterface.showError(con.getString(R.string.noresource))
                    }else if(response.code()==500){
                        superheroesInterface.showError(con.getString(R.string.servererror))
                    }
                }

            }

        })

        latch.await()
        return listaHeroes
    }

    private fun parseJson(resp: String?) {
        try {
            val jsonObject=JSONObject(resp)
            val data=jsonObject.getJSONObject("data")
            val results=data.getJSONArray("results")
            for (i in 0..results.length()) {

                val jsonHeroe=results.getJSONObject(i)
                val idCharacter=jsonHeroe.getInt("id")
                val name=jsonHeroe.getString("name")
                val imagen=jsonHeroe.getJSONObject("thumbnail").getString("path")+"."+jsonHeroe.getJSONObject("thumbnail").getString("extension")
                val desc=jsonHeroe.getString("description")
                var superHeroe=SuperHeroe(name,imagen,idCharacter,desc)
                listaHeroes.add(superHeroe)
            }
        } catch (e: Exception) {
            superheroesInterface.showError(con.getString(R.string.badrest))
        }

    }

    fun getDetallesSuperheroe(id: Int) {
        TODO("Not yet implemented")
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun addSuperheroes(offset: Int): List<SuperHeroe> {
        return getSuperheroes(offset)
    }
}