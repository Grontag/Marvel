package david.fernandez.marvel.view

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import david.fernandez.marvel.R
import david.fernandez.marvel.data.SuperheroesInterface
import david.fernandez.marvel.model.SuperHeroe
import david.fernandez.marvel.presenter.MarvelPresenter
import david.fernandez.marvel.view.adapter.RecyclerAdapter

class MainActivity : AppCompatActivity(), SuperheroesInterface {

    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var recyclerView : RecyclerView
    val mAdapter : RecyclerAdapter = RecyclerAdapter()
    private lateinit var presenter : MarvelPresenter
    lateinit var superheroes:List<SuperHeroe>
    lateinit var dialog : IndeterminateProgressDialog
    var offset=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter= MarvelPresenter(this)
        presenter.setContext(this)
        dialog= IndeterminateProgressDialog(this)
        dialog.setMessage(getString(R.string.loading))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        superheroes=presenter.getSuperheroes(0)
        recyclerView= RecyclerView(this)
        linearLayoutManager = LinearLayoutManager(this)

        setUpRecyclerView()
        dialog.dismiss()

    }

    fun setUpRecyclerView(){
        recyclerView = findViewById(R.id.rv) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(superheroes, this)
        recyclerView.adapter = mAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (linearLayoutManager.findLastVisibleItemPosition() >=totalItemCount-10) {
                    offset+=100
                    reloadRecyclerview(offset)
                }
            }
        })
    }

    fun reloadRecyclerview(offset:Int){
        dialog.show()
        superheroes=presenter.addSuperheroes(offset)
        recyclerView.adapter?.notifyDataSetChanged()
        dialog.dismiss()
    }

    override fun showError(error: String) {
        runOnUiThread{
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }

    }
}