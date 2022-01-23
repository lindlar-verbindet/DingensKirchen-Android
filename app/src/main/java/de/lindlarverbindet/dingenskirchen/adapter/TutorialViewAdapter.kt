package de.lindlarverbindet.dingenskirchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import de.lindlarverbindet.dingenskirchen.R

class TutorialViewAdapter(private val ctx: Context): RecyclerView.Adapter<TutorialViewAdapter.TutorialViewHolder>() {

    private val images: List<Int> = listOf(R.drawable.img_tutorial_1,
                                    R.drawable.img_tutorial_2,
                                    R.drawable.img_tutorial_3,
                                    R.drawable.img_tutorial_4,
                                    R.drawable.img_tutorial_5,
                                    R.drawable.img_tutorial_6,
                                    R.drawable.img_tutorial_7)

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