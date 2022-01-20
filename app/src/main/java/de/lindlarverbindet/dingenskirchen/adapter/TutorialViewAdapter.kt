package de.lindlarverbindet.dingenskirchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import de.lindlarverbindet.dingenskirchen.R

class TutorialViewAdapter(private val ctx: Context): RecyclerView.Adapter<TutorialViewAdapter.TutorialViewHolder>() {

    private val images: List<Int> = listOf(R.drawable.ic_tutorial1,
                                    R.drawable.ic_tutorial2,
                                    R.drawable.ic_tutorial3,
                                    R.drawable.ic_tutorial4,
                                    R.drawable.ic_tutorial5,
                                    R.drawable.ic_tutorial6)

    class TutorialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tutorialImage: ImageView = itemView.findViewById(R.id.tutorial_image) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        return TutorialViewHolder(LayoutInflater.from(ctx).inflate(R.layout.tutorial_item, parent, false))
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        holder.tutorialImage.setImageResource(images[position]) // .setImageDrawable(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

}