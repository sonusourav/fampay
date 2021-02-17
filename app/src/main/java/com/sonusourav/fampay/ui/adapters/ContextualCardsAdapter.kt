package com.sonusourav.fampay.ui.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.card.MaterialCardView
import com.sonusourav.fampay.R
import com.sonusourav.fampay.data.models.Card
import com.sonusourav.fampay.data.models.CardGroup
import com.sonusourav.fampay.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

const val IS_BIG_CARD_DISMISSED = "isBigCardDismissed"
const val PREF = "pref"

class ContextualCardsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cardGroupList: MutableList<CardGroup> = ArrayList()
    private var scrollable = false
    private lateinit var onItemRemoveListener: OnItemRemoveListener
    private lateinit var pref: SharedPref

    internal class NonScrollableLLViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nonScrollableLl: LinearLayout = itemView.findViewById(R.id.non_scrollable_ll)
    }

    internal class ScrollableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var scrollableHv: HorizontalScrollView = itemView.findViewById(R.id.horizontal_scrollable_item_hv)
        var scrollableLl: LinearLayout = itemView.findViewById(R.id.scrollable_ll)
    }

    internal class BigDisplayCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnLongClickListener {
        var bigCardViewRl: RelativeLayout
        var remindLaterCv: MaterialCardView
        var dismissNowCv: MaterialCardView
        var bigCard: MaterialCardView
        var bigCardDisplayTv: TextView
        var withActionTv: TextView
        var descriptionTv: TextView
        var imageView: ImageView
        var actionBtn: Button
        var start = 0.0f
        var end = 0.5f
        override fun onLongClick(v: View): Boolean {
            val animation: Animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,  //fromXType
                    start,  //fromXValue
                    Animation.RELATIVE_TO_SELF,  //toXType
                    end,  //toXValue
                    Animation.RELATIVE_TO_SELF,  //fromYType
                    0.0f,  //fromYValue
                    Animation.RELATIVE_TO_SELF,  //toYType
                    0.0f) //toYValue
            animation.duration = 500
            animation.fillAfter = true
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    val temp = start
                    start = end
                    end = temp
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            bigCard.startAnimation(animation)
            return true
        }

        init {
            itemView.setOnLongClickListener(this)
            bigCardViewRl = itemView.findViewById(R.id.big_card_items_rl)
            remindLaterCv = itemView.findViewById(R.id.remind_later_cv)
            dismissNowCv = itemView.findViewById(R.id.dismiss_now_cv)
            bigCard = itemView.findViewById(R.id.big_card)
            bigCardDisplayTv = itemView.findViewById(R.id.big_card_display_tv)
            imageView = itemView.findViewById(R.id.big_card_iv)
            withActionTv = itemView.findViewById(R.id.with_action_tv)
            descriptionTv = itemView.findViewById(R.id.description_tv)
            actionBtn = itemView.findViewById(R.id.big_card_action_btn)
            bigCardViewRl.visibility = View.VISIBLE
        }
    }

    internal class SmallDisplayCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var smallDisplayCard: MaterialCardView = itemView.findViewById(R.id.small_display_card)
        var smallDisplayCardIv: CircleImageView = itemView.findViewById(R.id.small_display_card_iv)
        var smallDisplayCardTv: TextView = itemView.findViewById(R.id.small_display_card_tv)
        var aryaStarkTv: TextView = itemView.findViewById(R.id.arya_stark_tv)

    }

    internal class DynamicCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dynamicCard: MaterialCardView = itemView.findViewById(R.id.dynamic_card)
        var dynamicCardLl: LinearLayout = itemView.findViewById(R.id.dynamic_card_ll)
    }

    internal class ImageCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageCardLl: LinearLayout
        var imageCard: MaterialCardView

        init {
            imageCard = itemView.findViewById(R.id.image_card)
            imageCardLl = itemView.findViewById(R.id.image_card_ll)
        }
    }

    internal class SmallCardWithArrowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var smallArrowCard: MaterialCardView
        var smallArrowCardTv: TextView

        init {
            smallArrowCard = itemView.findViewById(R.id.small_arrow_card)
            smallArrowCardTv = itemView.findViewById(R.id.small_card_with_arrow_tv)
        }
    }

    fun setCardGroupList(cardGroupList: MutableList<CardGroup>) {
        this.cardGroupList = cardGroupList
        notifyDataSetChanged()
    }

    fun setOnItemRemoveListener(onItemRemoveListener: OnItemRemoveListener?) {
        if (onItemRemoveListener != null) {
            this.onItemRemoveListener = onItemRemoveListener
        }
    }

    fun addData(cardGroups: List<CardGroup>?) {
        cardGroupList.addAll(cardGroups!!)
        notifyDataSetChanged()
    }

    private fun layNonScrollableBigCards(nonScrollableLLViewHolder: NonScrollableLLViewHolder,
                                         cards: List<Card>?, position: Int) {
        for (card in cards!!) {
            val bigDisplayCardViewHolder = BigDisplayCardViewHolder(LayoutInflater.from(nonScrollableLLViewHolder.itemView.context)
                    .inflate(R.layout.big_card_item_layout, nonScrollableLLViewHolder.itemView as ViewGroup, false))
            bigDisplayCardViewHolder.bigCardViewRl.visibility = View.VISIBLE
            bigDisplayCardViewHolder.bigCard.setCardBackgroundColor(Color
                    .parseColor(card.bgColor))

            Glide.with(bigDisplayCardViewHolder.itemView)
                    .load(card.bgImage!!.imageUrl)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            bigDisplayCardViewHolder.imageView.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            bigDisplayCardViewHolder.imageView.background = placeholder
                        }
                    })
            if(!card.formattedDescription?.entities.isNullOrEmpty()){
                bigDisplayCardViewHolder.descriptionTv.text = card.formattedDescription
                        ?.entities!![0].text
                bigDisplayCardViewHolder.descriptionTv.setTextColor(Color.parseColor(
                        card.formattedDescription!!
                                .entities!![0].color
                ))
            }

            if(!card.formattedTitle?.entities.isNullOrEmpty()){
                bigDisplayCardViewHolder.bigCardDisplayTv.text = card.formattedTitle?.entities!![0].text
                bigDisplayCardViewHolder.bigCardDisplayTv.setTextColor(Color.parseColor(
                        card.formattedTitle!!.entities?.get(0)?.color
                ))
                bigDisplayCardViewHolder.withActionTv.text = card.formattedTitle!!
                        .entities!![1].text
                bigDisplayCardViewHolder.withActionTv.setTextColor(Color.parseColor(
                        card.formattedTitle!!
                                .entities!![1].color
                ))
            }

            bigDisplayCardViewHolder.actionBtn.text = card.cta!![0].text
            bigDisplayCardViewHolder.actionBtn.setTextColor(Color.parseColor(card.cta!![0].textColor))
            bigDisplayCardViewHolder.actionBtn.setBackgroundColor(Color.parseColor(card.cta!![0].bgColor))
            bigDisplayCardViewHolder.remindLaterCv.setOnClickListener { v: View? ->
                bigDisplayCardViewHolder.bigCardViewRl.visibility = View.GONE
                onItemRemoveListener.onItemRemoved(position)
            }
            bigDisplayCardViewHolder.dismissNowCv.setOnClickListener { v: View? ->
                bigDisplayCardViewHolder.bigCardViewRl.visibility = View.GONE
                onItemRemoveListener.onItemRemoved(position)
                pref.saveDismiss(PREF, IS_BIG_CARD_DISMISSED, true)
            }
            nonScrollableLLViewHolder.nonScrollableLl.addView(bigDisplayCardViewHolder.itemView)
        }
    }

    private fun layNonScrollableSmallArrowCard(nonScrollableLLViewHolder: NonScrollableLLViewHolder,
                                               cards: List<Card>?) {
        for (card in cards!!) {
            val smallCardWithArrowViewHolder = SmallCardWithArrowViewHolder(LayoutInflater.from(nonScrollableLLViewHolder.itemView.context)
                    .inflate(R.layout.small_card_with_arrow_item_layout, nonScrollableLLViewHolder.itemView as ViewGroup, false))
            //smallCardWithArrowViewHolder.smallArrowCard.setCardBackgroundColor(Color.parseColor(card.bgColor))
            smallCardWithArrowViewHolder.smallArrowCardTv.text = card.title
            nonScrollableLLViewHolder.nonScrollableLl.addView(smallCardWithArrowViewHolder.itemView)
        }
    }

    private fun layNonScrollableSmallDisplayCard(nonScrollableLLViewHolder: NonScrollableLLViewHolder,
                                                 cards: List<Card>?) {
        for (card in cards!!) {
            val smallDisplayCardViewHolder = SmallDisplayCardViewHolder(LayoutInflater.from(nonScrollableLLViewHolder.itemView.context)
                    .inflate(R.layout.small_display_card_item_layout, nonScrollableLLViewHolder.itemView as ViewGroup, false))
            //smallDisplayCardViewHolder.smallDisplayCard.setCardBackgroundColor(Color.parseColor(card.bgColor))
            smallDisplayCardViewHolder.smallDisplayCardTv.text = card.title // could be formatted title
            smallDisplayCardViewHolder.aryaStarkTv.text = card.description // could be formatted desc
            Glide.with(smallDisplayCardViewHolder.itemView)
                    .load(card.icon!!.imageUrl)
                    .into(smallDisplayCardViewHolder.smallDisplayCardIv)
            nonScrollableLLViewHolder.nonScrollableLl.addView(smallDisplayCardViewHolder.itemView)
        }
    }

    private fun layScrollableSmallDisplayCard(scrollableViewHolder: ScrollableViewHolder,
                                              cards: List<Card>?) {
        for (card in cards!!) {
            val smallDisplayCardViewHolder = SmallDisplayCardViewHolder(LayoutInflater.from(scrollableViewHolder.itemView.context)
                    .inflate(R.layout.small_display_card_item_layout, scrollableViewHolder.itemView as ViewGroup, false))
            //smallDisplayCardViewHolder.smallDisplayCard.setCardBackgroundColor(Color.parseColor(card.bgColor))
            smallDisplayCardViewHolder.smallDisplayCardTv.text = card.title // could be formatted title
            smallDisplayCardViewHolder.aryaStarkTv.text = card.description // could be formatted desc
            Glide.with(smallDisplayCardViewHolder.itemView)
                    .load(card.icon!!.imageUrl)
                    .into(smallDisplayCardViewHolder.smallDisplayCardIv)
            scrollableViewHolder.scrollableLl.addView(smallDisplayCardViewHolder.itemView)
        }
    }

    private fun layScrollableDynamicCard(scrollableViewHolder: ScrollableViewHolder,
                                              cards: List<Card>?) {
        for (card in cards!!) {
            val dynamicCardViewHolder = DynamicCardViewHolder(LayoutInflater.from(scrollableViewHolder.itemView.context)
                    .inflate(R.layout.dynamic_card_item_layout, scrollableViewHolder.itemView as ViewGroup, false))
            Glide.with(dynamicCardViewHolder.itemView)
                    .load(card.bgImage!!.imageUrl)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            dynamicCardViewHolder.dynamicCardLl.background = resource
                            val height = resource.intrinsicHeight
                            val width = height * card.bgImage!!.aspectRatio
                            dynamicCardViewHolder.dynamicCardLl.layoutParams.width = width.toInt()
                            dynamicCardViewHolder.dynamicCardLl.layoutParams.height = height
                            dynamicCardViewHolder.dynamicCardLl.requestLayout()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            dynamicCardViewHolder.dynamicCardLl.background = placeholder
                        }
                    })


            scrollableViewHolder.scrollableLl.addView(dynamicCardViewHolder.itemView)
        }
    }

    private fun layScrollableImageCard(scrollableLLViewHolder: ScrollableViewHolder,
                                       cards: List<Card>?) {
        for (card in cards!!) {
            val imageCardViewHolder = ImageCardViewHolder(LayoutInflater.from(scrollableLLViewHolder.itemView.context)
                    .inflate(R.layout.image_card_item_layout, scrollableLLViewHolder.itemView as ViewGroup, false))
            Glide.with(imageCardViewHolder.itemView)
                    .load(card.bgImage!!.imageUrl)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            imageCardViewHolder.imageCardLl.background = resource
                            val height = resource.intrinsicHeight
                            val width = height * card.bgImage!!.aspectRatio
                            imageCardViewHolder.imageCardLl.layoutParams.width = width.toInt()
                            imageCardViewHolder.imageCardLl.layoutParams.height = height
                            imageCardViewHolder.imageCardLl.requestLayout()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            imageCardViewHolder.imageCardLl.background = placeholder
                        }
                    })

            scrollableLLViewHolder.scrollableLl.addView(imageCardViewHolder.itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var itemViewTypeNo = 3 //Big Display Card Default
        scrollable = cardGroupList[position].isScrollable!!
        when (cardGroupList[position].designType) {
            "HC3" -> itemViewTypeNo = 3 // Big Display Card
            "HC1" -> itemViewTypeNo = 1 // Small Display Card
            "HC9" -> itemViewTypeNo = 9 // Dynamic Card
            "HC5" -> itemViewTypeNo = 5 // Image Card
            "HC6" -> itemViewTypeNo = 6 // Small Card with Arrow
        }
        return itemViewTypeNo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        pref = SharedPref(parent.context)
        return if (!scrollable) NonScrollableLLViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.horizontal_non_scrollable_item_layout, parent, false)) else ScrollableViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.horizontal_scrollable_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> if (!scrollable) layNonScrollableSmallDisplayCard(holder as NonScrollableLLViewHolder,
                    cardGroupList[position].cards) else layScrollableSmallDisplayCard(holder as ScrollableViewHolder,
                    cardGroupList[position].cards)
            3 -> if(!pref.isDismissed(PREF,IS_BIG_CARD_DISMISSED)){
                layNonScrollableBigCards(holder as NonScrollableLLViewHolder, cardGroupList[position].cards,position)
            }
            9 -> layScrollableDynamicCard(holder as ScrollableViewHolder, cardGroupList[position].cards)
            5 -> layScrollableImageCard(holder as ScrollableViewHolder,
                    cardGroupList[position].cards)
            6 -> layNonScrollableSmallArrowCard(holder as NonScrollableLLViewHolder,
                    cardGroupList[position].cards)
        }
    }

    override fun getItemCount(): Int {
        return cardGroupList.size
    }

    interface OnItemRemoveListener {
        fun onItemRemoved(position: Int)
    }
}