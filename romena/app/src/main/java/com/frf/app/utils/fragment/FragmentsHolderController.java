package com.frf.app.utils.fragment;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.frf.app.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class FragmentsHolderController {
    private FragmentManager mFragManager;       // Fragment manager
    private Fragment currentFragment;           // Fragmento atual
    private String currentModuleFragment;       // Tag do modulo atual
    private int currentLayoutTag;            // Tag do layout dos fragmentos
    private Hashtable<String, List<Fragment>> fragmentsModuleList;  //Lista de fragmentos separada por modulos
    private String FRAGMENT_BACKSTACK = "frag_backstac_custom";

    public FragmentsHolderController (FragmentManager manager, int layoutTag){
        this.mFragManager = manager;
        this.currentLayoutTag = layoutTag;
        fragmentsModuleList = new Hashtable<>();
    }

    //Caso tenha apertado para voltar
    public boolean onBackPressed (){
        boolean hasFragmentToGo  = false;
        if(getFragmentsModuleCount(currentModuleFragment) > 1){
            hasFragmentToGo = true;
            removeFragmentToModule(currentModuleFragment);
            Fragment fragment = getFirtFragmentByModule(currentModuleFragment);
            addFragmentInFrameSimple(fragment, fragment.getTag(), currentModuleFragment, R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return hasFragmentToGo;
    }

    //Adiciona um fragmento ao container
    public void addFragmentInFrame (Fragment fragment, String tag, @Nullable String module){
        FragmentTransaction transaction = mFragManager.beginTransaction();
        Fragment oldFragment = getFragmentByTag(tag);
        mFragManager.popBackStack();
        Log.d("FragmentController", mFragManager.getFragments().toString());

        if(mFragManager.getFragments().size() > 0){
            if (checkIfFragmentExists(tag)){
                Log.d("FragmentController", "old: " + oldFragment.toString());
                transaction.replace(currentLayoutTag, oldFragment, tag);
            } else {
                transaction.replace(currentLayoutTag, fragment, tag);
                Log.d("FragmentController", "fragment: " + fragment.toString());
                transaction.addToBackStack(tag);// esta dando alguns erros na instancia de viewmodel
            }
        }else{
            Log.d("FragmentController", "new : " +fragment.toString());
            transaction.add(currentLayoutTag, fragment, tag);
        }

        currentFragment = fragment;
        currentModuleFragment = module != null? module:currentModuleFragment;
        addFragmentToModule(fragment, currentModuleFragment);
        transaction.commit();
    }

    public void addFragmentInFrameSimple (Fragment fragment, String tag, @Nullable String module){
        try{
            FragmentTransaction transaction = mFragManager.beginTransaction();
            transaction.replace(currentLayoutTag, fragment, tag);
            currentFragment = fragment;
            currentModuleFragment = module != null? module:currentModuleFragment;
            addFragmentToModule(fragment, currentModuleFragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addFragmentInFrameSimple (Fragment fragment, String tag, @Nullable String module, int animation_in, int animation_out){
        try{
            FragmentTransaction transaction = mFragManager.beginTransaction();
            transaction.setCustomAnimations(
                    animation_in,
                    animation_out,
                    animation_in,
                    animation_out
            );
            transaction.replace(currentLayoutTag, fragment, tag);
            transaction.addToBackStack(null);
            currentFragment = fragment;
            currentModuleFragment = module != null? module:currentModuleFragment;
            addFragmentToModule(fragment, currentModuleFragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addFragmentInFrameAttach (Fragment fragment, String tag, @Nullable String module){
        FragmentTransaction transaction = mFragManager.beginTransaction();
        transaction.detach(currentFragment);
        transaction.attach(fragment);
        currentFragment = fragment;
        currentModuleFragment = module != null? module:currentModuleFragment;
        addFragmentToModule(fragment, currentModuleFragment);
        transaction.commit();
    }

    //Usado para mudar de fragmento por um modulo como contexto
    public void changeToFragmentContext (Fragment fragment, String tag, String module, boolean reset){
        int moduleCount = getFragmentsModuleCount(module);
        if(moduleCount > 0){
            if (reset) {
                removeAllFragmentToModule(module);
            }
            fragment = getFirtFragmentByModule(fragment, module, reset);
            addFragmentInFrameSimple(fragment, fragment.getTag(), module);
        }else{
            addFragmentInFrameSimple(fragment, tag, module);
        }
    }

    //Pega o fragmento atual
    public Fragment getCurrentFragment(){
        return this.currentFragment;
    }

    public void setCurrentModuleFragment(String module){
        this.currentModuleFragment = module;
    }

    void addFragmentToModule (Fragment fragment, String tag){
        List<Fragment> fragmentList = new ArrayList<>();
        if(fragmentsModuleList.containsKey(tag)){
            fragmentList = fragmentsModuleList.get(tag);
            if(!fragmentList.contains(fragment))
                fragmentList.add(fragment);
        }else{
            fragmentList.add(fragment);
        }

        fragmentsModuleList.put(tag, fragmentList);
    }

    void removeFragmentToModule (String tag){
        List<Fragment> fragmentList = new ArrayList<>();
        if(fragmentsModuleList.containsKey(tag)){
            fragmentList = fragmentsModuleList.get(tag);
            fragmentList.remove(fragmentList.size()-1);
        }
        fragmentsModuleList.put(tag, fragmentList);
    }

    void removeAllFragmentToModule (String tag){
        List<Fragment> fragmentList = new ArrayList<>();
        if(fragmentsModuleList.containsKey(tag)){
            fragmentList = fragmentsModuleList.get(tag);
            fragmentList.removeAll(fragmentList);
        }
        fragmentsModuleList.put(tag, fragmentList);
    }

    Fragment getFirtFragmentByModule (Fragment fragment, String module, boolean reset){
        if(fragmentsModuleList.containsKey(module)){
            List<Fragment> fragmentList = fragmentsModuleList.get(module);
            if(fragmentList != null && fragmentList.size() > 0){
                fragment = fragmentList.get(reset? 0: fragmentList.size()-1);
                return fragment;
            }else{
                addFragmentToModule(fragment, module);
                return getFirtFragmentByModule(fragment, module, reset);
            }
        }else{
            addFragmentToModule(fragment, module);
            return getFirtFragmentByModule(fragment, module, reset);
        }
    }

    Fragment getFirtFragmentByModule (String module){
        Fragment fragment = null;
        if(fragmentsModuleList.containsKey(module)){
            List<Fragment> fragmentList = fragmentsModuleList.get(module);
            fragment = fragmentList.get(fragmentList.size()-1);
        }
        return fragment;
    }

    int getFragmentsModuleCount (String module){

        if(fragmentsModuleList != null && module != null && !module.isEmpty()){
            if(fragmentsModuleList.containsKey(module)){
                List<Fragment> fragmentList = fragmentsModuleList.get(module);
                return fragmentList.size();
            }else{
                return 0;
            }
        }

        return 0;
    }

    //Verficica se esse fragmento existe na lista de fragmentos
    boolean checkIfFragmentExists (String tag){
        Log.d("FragmentController", "tag: " + tag + " = " +  (getFragmentByTag(tag) != null));
        return getFragmentByTag(tag) != null;
    }

    Fragment getFragmentByTag (String tag){
        return mFragManager.findFragmentByTag(tag);
    }

    Fragment getActiveFragment() {
        int count = mFragManager.getBackStackEntryCount();
        Fragment frag = mFragManager.getFragments().get(0);
        return frag;
    }
}
