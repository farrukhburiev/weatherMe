package farrukh.weatherme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import farrukh.weatherme.R
import farrukh.weatherme.data_class.Hour


class HourAdapter(
    var array: MutableList<Hour>,
) : RecyclerView.Adapter<HourAdapter.MyHolder>() {

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time = itemView.findViewById<TextView>(R.id.time)
        var wind = itemView.findViewById<TextView>(R.id.wind_kph)
        var temp = itemView.findViewById<TextView>(R.id.temp)
        var img = itemView.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.hour_layout, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        var position = array.get(position)

        holder.time.text = position.time
        holder.wind.text = position.wind+" kph"
        holder.temp.text = position.temp+" °C"
        holder.img.load(position.img)
    }
}

