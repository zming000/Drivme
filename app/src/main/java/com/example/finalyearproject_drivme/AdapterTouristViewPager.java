package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.finalyearproject_drivme.TouristActivityFragments.TouristBookingFragment;
import com.example.finalyearproject_drivme.TouristActivityFragments.TouristHistoryFragment;
import com.example.finalyearproject_drivme.TouristActivityFragments.TouristOngoingFragment;

public class AdapterTouristViewPager extends FragmentStateAdapter {
    public AdapterTouristViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //change the view
        switch (position){
            case 1:
                return new TouristOngoingFragment();
            case 2:
                return new TouristHistoryFragment();
            case 0:
            default:
                return new TouristBookingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
