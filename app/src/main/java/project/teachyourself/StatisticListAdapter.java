package project.teachyourself;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.teachyourself.model.Category;

/**
 * Adapters provide a binding from an app-specific data set to views
 * that are displayed within a RecyclerView.
 */
public class StatisticListAdapter
        extends RecyclerView.Adapter<StatisticListAdapter.StatisticViewHolder>
        implements View.OnClickListener{

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class StatisticViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemTitle;
        private final TextView itemScore;
        private final LinearLayout itemExpandable;
        private final PieChart scoreChart;
        private final PieChart timeChart;

        public StatisticViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitleLabel);
            itemScore = itemView.findViewById(R.id.itemScoreLabel);
            itemExpandable = itemView.findViewById(R.id.itemExpandable);
            scoreChart = itemView.findViewById(R.id.itemChartScore);
            timeChart = itemView.findViewById(R.id.itemChartTime);
        }
    }

    private LayoutInflater inflater;
    private List<Category> categories;
    private int expandedPosition = -1;
    private Context context;

    /**
     * Constructor
     * @param context the context
     */
    public StatisticListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * Called when RecyclerView needs a new {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * of the given type to represent an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, List)}.
     * Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public StatisticViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.statistic_item, parent, false);
        StatisticViewHolder holder =  new StatisticViewHolder(itemView);

        // Add a click listener and set the holder to be retrieved from the view
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(holder);
        return holder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link android.support.v7.widget.RecyclerView.ViewHolder#itemView}
     * to reflect the item at the given position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener),
     * use {@link android.support.v7.widget.RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, List)}
     * instead if Adapter can handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final StatisticViewHolder holder, final int position) {
        if (categories != null) {
            final Category stat = categories.get(position);
            holder.itemTitle.setText(stat.getTitle());
            if (stat.getQuestions() > 0)
                holder.itemScore.setText(String.format(context.getString(R.string.score_percent),
                        stat.getScore() * 100 / stat.getQuestions()));
            else
                holder.itemScore.setText(R.string.no_score);

            // Expand the view clicked to display charts
            if (position == expandedPosition) {
                holder.itemExpandable.setVisibility(View.VISIBLE);
                displayScore(holder, stat);
                displayTimeAverage(holder, stat);
            } else {
                holder.itemExpandable.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        StatisticViewHolder holder = (StatisticViewHolder) v.getTag();

        // Reduce the previously expanded item
        if (expandedPosition >= 0) {
            int prev = expandedPosition;
            notifyItemChanged(prev);
        }
        // Expand clicked item or reduce it if it was already expanded
        if (expandedPosition != holder.getPosition()) {
            expandedPosition = holder.getPosition();
            notifyItemChanged(expandedPosition);
        } else {
            expandedPosition = -1;
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (categories != null)
            return categories.size();
        return 0;
    }

    /**
     * Set the List of categories to display in the RecyclerView
     * @param categories List of categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    /**
     * Get the Category at a given position in the RecyclerView
     * @param position position in the RecyclerView
     * @return the Category at the position
     */
    public Category getCategoryAtPosition(int position) {
        if (categories != null && categories.size() > position)
            return categories.get(position);
        else
            return null;
    }

    /**
     * Initialise the score Pie Chart
     */
    private void displayScore(StatisticViewHolder holder, Category category) {
        int wrong = category.getQuestions() - category.getScore();

        // Set all entries
        List<PieEntry>  entries = new ArrayList<>();
        entries.add(new PieEntry(category.getScore(), "(" + category.getScore() + ")"));
        entries.add(new PieEntry(wrong, "(" + wrong + ")"));

        // Entry specific styling
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(new int[] {R.color.good, R.color.wrong}, context);
        dataSet.setValueTextColor(context.getResources().getColor(R.color.white));
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(0f);
        dataSet.setValueFormatter(new PercentFormatter(new DecimalFormat("###,###,##0")));

        // Chart general styling
        holder.scoreChart.setData(new PieData(dataSet));
        holder.scoreChart.setDescription(null);
        holder.scoreChart.setDrawEntryLabels(true);
        holder.scoreChart.setEntryLabelTextSize(8f);
        holder.scoreChart.setDrawHoleEnabled(true);
        holder.scoreChart.setUsePercentValues(true);
        holder.scoreChart.setCenterText(category.getQuestions() + "");
        holder.scoreChart.setCenterTextSize(14f);
        holder.scoreChart.setHoleRadius(45f);
        holder.scoreChart.setHoleColor(context.getResources().getColor(R.color.white));
        holder.scoreChart.setTransparentCircleRadius(0f);
        holder.scoreChart.getLegend().setEnabled(false);
        holder.scoreChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        holder.scoreChart.invalidate();
    }

    /**
     * Initialise the time response average Pie Chart
     */
    private void displayTimeAverage(StatisticViewHolder holder, Category category) {

        // Set all entries
        List<PieEntry> entriesTime = new ArrayList<>();
        entriesTime.add(new PieEntry(category.getTimeAvg()));
        entriesTime.add(new PieEntry(QuizActivity.TIMER - category.getTimeAvg()));

        // Entry specific styling
        PieDataSet dataSet = new PieDataSet(entriesTime, null);
        dataSet.setColors(new int[] {R.color.good, R.color.grey}, context);
        dataSet.setSelectionShift(0f);
        dataSet.setDrawValues(false);

        // Chart general styling
        holder.timeChart.setData(new PieData(dataSet));
        holder.timeChart.setDescription(null);
        holder.timeChart.setDrawEntryLabels(false);
        holder.timeChart.setDrawHoleEnabled(true);
        holder.timeChart.setCenterText(String.format(Locale.US,
                context.getString(R.string.chart_time_average),
                category.getTimeAvg()));
        holder.timeChart.setCenterTextSize(16f);
        holder.timeChart.setHoleRadius(55f);
        holder.timeChart.setHoleColor(context.getResources().getColor(R.color.white));
        holder.timeChart.setTransparentCircleRadius(0f);
        holder.timeChart.getLegend().setEnabled(false);
        holder.timeChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        holder.timeChart.invalidate();
    }
}
