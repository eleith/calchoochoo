package com.eleith.calchoochoo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import javax.inject.Inject;

public class ChooChooFragmentManager {

  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;

  @Inject
  public ChooChooFragmentManager(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
  }

  private FragmentTransaction getTransaction() {
    if (fragmentTransaction == null) {
      fragmentTransaction = fragmentManager.beginTransaction();
    }
    return fragmentTransaction;
  }

  public void updateTopAndBottomFragments(Fragment f1, Fragment f2) {
    updateTopFragment(f1);
    updateBottomFragment(f2);
  }

  public void updateTopFragment(Fragment fragment) {
    Fragment header = fragmentManager.findFragmentById(R.id.homeTopFragmentContainer);
    FragmentTransaction ft = getTransaction();

    if (header != null) {
      if (fragment == null) {
        ft.remove(header);
      } else {
        ft.replace(R.id.homeTopFragmentContainer, fragment);
      }
    } else if (fragment != null) {
      ft.add(R.id.homeTopFragmentContainer, fragment);
    }
  }

  public void updateBottomFragment(Fragment fragment) {
    Fragment main = fragmentManager.findFragmentById(R.id.homeFragmentContainer);
    FragmentTransaction ft = getTransaction();

    if (main != null) {
      if (fragment == null) {
        ft.remove(main);
      } else {
        ft.replace(R.id.homeFragmentContainer, fragment);
      }
    } else if (fragment != null) {
      ft.add(R.id.homeFragmentContainer, fragment);
    }
  }

  public void commit() {
    if (fragmentTransaction != null) {
      fragmentTransaction.addToBackStack(null);
      fragmentTransaction.commit();
      fragmentTransaction = null;
    }
  }
}
