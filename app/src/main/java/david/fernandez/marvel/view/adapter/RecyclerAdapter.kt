package david.fernandez.marvel.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import david.fernandez.marvel.R
import david.fernandez.marvel.model.SuperHeroe

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    lateinit var ctx:Context
    lateinit var superHeroes:List<SuperHeroe>

    fun RecyclerAdapter(listaHeroes:List<SuperHeroe>, ctx:Context){
        this.superHeroes=listaHeroes
        this.ctx=ctx
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.superhero_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        val item = superHeroes.get(position)
        holder.bind(item, ctx)
    }

    override fun getItemCount(): Int {
        return superHeroes.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val superheroName = view.findViewById(R.id.name) as TextView
        val desc = view.findViewById(R.id.desc) as TextView
        val avatar = view.findViewById(R.id.thumbnail) as ImageView
        fun bind(superhero:SuperHeroe, context: Context){
            superheroName.text = superhero.nombre
            desc.text = superhero.desc
            loadUrl(superhero.imagen, avatar, context)

        }
        fun loadUrl(url: String, target:ImageView, ctx:Context) {
            Picasso.with(ctx).load(url).into(target)
        }
    }
}