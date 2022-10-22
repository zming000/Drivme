package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.finalyearproject_drivme.DriverActivityFragments.DriverHistoryFragment;
import com.example.finalyearproject_drivme.DriverActivityFragments.DriverRequestFrament;
import com.example.finalyearproject_drivme.DriverActivityFragments.DriverOngoingFragment;

public class AdapterDriverViewPager extends FragmentStateAdapter {
    public AdapterDriverViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //changing the view
        switch (position){
            case 1:
                return new DriverOngoingFragment();
            case 2:
                return new DriverHistoryFragment();
            case 0:
            default:
                return new DriverRequestFrament();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
