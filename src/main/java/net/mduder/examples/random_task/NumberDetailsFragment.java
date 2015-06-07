package net.mduder.examples.random_task;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A fragment representing a list of numbers, each in a separate language.
 */
public class NumberDetailsFragment extends ListFragment {
    private static final String[] languages =
            {"English", "Tamil", "Chinese", "Roman", "Devanagari", "Arabic", "Thai"};
    private static final String ARG_NUMBER = "arg_number";

    public static NumberDetailsFragment newInstance(int number) {
        NumberDetailsFragment numberDetailsFragment = new NumberDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER, number);
        numberDetailsFragment.setArguments(args);
        return numberDetailsFragment;
    }

    public NumberDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ArrayList<String> listElements = new ArrayList<String>();

        super.onCreate(savedInstanceState);
        String num = Integer.toString(getArguments().getInt(ARG_NUMBER));

        for (String language : languages) {
            int resource_ID = getResources().getIdentifier(
                    language + "_" + num, "string", BuildConfig.APPLICATION_ID);
            listElements.add(language + ": " + getString(resource_ID));
        }

        /* onCreateView() automagically handled */
        setListAdapter(new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, listElements));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
