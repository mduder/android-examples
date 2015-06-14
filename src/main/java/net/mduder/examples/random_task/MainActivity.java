package net.mduder.examples.random_task;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * Started at launch, this activity will load the main UI fragment on creation.
 * This also handles all EventBus messages, launching new UIs as requested.
 */
public class MainActivity extends ActionBarActivity {
    private TreeMap<String, Object> exchangeRateDataCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            String fragmentId = getResources().getText(R.string.fragment_main).toString();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_main, new MainFragment(), fragmentId).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_menu_main:
                launchFragmentMain();
                return true;
            case R.id.action_bar_number_details:
                launchFragmentNumberDetails();
                return true;
            case R.id.action_bar_stop_watch:
                launchFragmentStopWatch();
                return true;
            case R.id.action_bar_mortgage_loan:
                launchFragmentMortgageLoanCalc();
                return true;
            case R.id.action_bar_exchange_rate:
                launchFragmentExchangeRateCalc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("exchangeRateDataCache", exchangeRateDataCache);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        exchangeRateDataCache = (TreeMap<String, Object>)savedInstanceState
                .getSerializable("exchangeRateDataCache");
    }

    /* Called implicitly by EventBus as the registrar of the default channel */
    public void onEventMainThread(EventBusMessage message) {
        switch (message.getMessageType()) {
            case launchFragmentNumberDetails:
                launchFragmentNumberDetails();
                break;
            case launchFragmentStopWatch:
                launchFragmentStopWatch();
                break;
            case launchFragmentMortgageLoanCalc:
                launchFragmentMortgageLoanCalc();
                break;
            case launchFragmentExchangeRateCalc:
                launchFragmentExchangeRateCalc();
                break;
            case getDataExchangeRate:
                getDataExchangeRate();
                break;
            case setDataExchangeRate:
                setDataExchangeRate(message.getArgs());
                break;
        }
    }

    /* Re-use fragment if it already exists - otherwise, create a new one */
    private void launchFragmentMain() {
        MainFragment mainFragment = null;
        String fragmentId = getResources().getText(R.string.fragment_main).toString();
        Fragment maybeFragment = getSupportFragmentManager().findFragmentByTag(fragmentId);
        if (maybeFragment == null) {
            mainFragment = MainFragment.newInstance();
        } else {
            mainFragment = (MainFragment)maybeFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_main, mainFragment, fragmentId)
                .addToBackStack(fragmentId).commit();
    }

    /* Re-use fragment if it already exists - otherwise, create a new one */
    private void launchFragmentNumberDetails() {
        NumberDetailsFragment numberDetailsFragment = null;
        String fragmentId = getResources().getText(R.string.fragment_number_details).toString();
        Fragment maybeFragment = getSupportFragmentManager().findFragmentByTag(fragmentId);
        if (maybeFragment == null) {
            String mainId = getResources().getText(R.string.fragment_main).toString();
            MainFragment mainFrag = (MainFragment)getSupportFragmentManager().findFragmentByTag(mainId);
            numberDetailsFragment = NumberDetailsFragment.newInstance(mainFrag.getCubeFaceValue());
        } else {
            numberDetailsFragment = (NumberDetailsFragment)maybeFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_main, numberDetailsFragment, fragmentId)
                .addToBackStack(fragmentId).commit();
    }

    /* Re-use fragment if it already exists - otherwise, create a new one */
    private void launchFragmentStopWatch() {
        StopWatchFragment stopWatchFragment = null;
        String fragmentId = getResources().getText(R.string.fragment_stop_watch).toString();
        Fragment maybeFragment = getSupportFragmentManager().findFragmentByTag(fragmentId);
        if (maybeFragment == null) {
            stopWatchFragment = StopWatchFragment.newInstance();
        } else {
            stopWatchFragment = (StopWatchFragment)maybeFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_main, stopWatchFragment, fragmentId)
                .addToBackStack(fragmentId).commit();
    }

    /* Re-use fragment if it already exists - otherwise, create a new one */
    private void launchFragmentMortgageLoanCalc() {
        MortgageLoanCalcFragment mortgageLoanCalcFragment = null;
        String fragmentId = getResources().getText(R.string.fragment_mortgage_loan_calc).toString();
        Fragment maybeFragment = getSupportFragmentManager().findFragmentByTag(fragmentId);
        if (maybeFragment == null) {
            mortgageLoanCalcFragment = MortgageLoanCalcFragment.newInstance();
        } else {
            mortgageLoanCalcFragment = (MortgageLoanCalcFragment)maybeFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_main, mortgageLoanCalcFragment, fragmentId)
                .addToBackStack(fragmentId).commit();
    }

    /* Re-use fragment if it already exists - otherwise, create a new one */
    private void launchFragmentExchangeRateCalc() {
        ExchangeRateCalcFragment exchangeRateCalcFragment = null;
        String fragmentId = getResources().getText(R.string.fragment_exchange_rate_calc).toString();
        Fragment maybeFragment = getSupportFragmentManager().findFragmentByTag(fragmentId);
        if (maybeFragment == null) {
            exchangeRateCalcFragment = ExchangeRateCalcFragment.newInstance();
        } else {
            exchangeRateCalcFragment = (ExchangeRateCalcFragment)maybeFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_main, exchangeRateCalcFragment, fragmentId)
                .addToBackStack(fragmentId).commit();
    }

    /* Launch I/O thread to retrieve data unless data cached */
    private void getDataExchangeRate() {
        if (exchangeRateDataCache == null) {
            new GetDataExchangeRateTask().execute(
                    getResources().getString(R.string.exchange_rate_JSON));
        } else {
            setDataExchangeRate(exchangeRateDataCache);
        }
    }

    /**
     * First, cache the received exchange rate data.  Then,
     * set this data in requesting fragment, if it still exists.
     */
    private void setDataExchangeRate(TreeMap<String, Object> exchangeRateData) {
        if (exchangeRateDataCache != exchangeRateData) {
            exchangeRateDataCache = exchangeRateData;
        }
        ExchangeRateCalcFragment exchangeRateCalcFragment = null;
        String fragmentId = getResources().getText(R.string.fragment_exchange_rate_calc).toString();
        Fragment maybeFragment = getSupportFragmentManager().findFragmentByTag(fragmentId);
        if (maybeFragment == null) {
            return;
        }
        exchangeRateCalcFragment = (ExchangeRateCalcFragment)maybeFragment;
        exchangeRateCalcFragment.setDataExchangeRate(exchangeRateData);
    }
}
