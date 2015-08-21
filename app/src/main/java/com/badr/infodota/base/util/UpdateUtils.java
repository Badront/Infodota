package com.badr.infodota.base.util;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.base.activity.BaseActivity;
import com.badr.infodota.base.remote.BaseRemoteServiceImpl;
import com.badr.infodota.base.service.update.UpdateService;

/**
 * Created by Badr on 17.02.2015.
 */
public class UpdateUtils {
    public static void checkNewVersion(final Context context,final boolean userInitiative) {
        if(BaseRemoteServiceImpl.isNetworkAvailable(context))
        {
            ProgressTask<Pair<Boolean,String>> task=new ProgressTask<Pair<Boolean, String>>() {
                @Override
                public Pair<Boolean, String> doTask(OnPublishProgressListener listener) throws Exception {
                    UpdateService updateService= BeanContainer.getInstance().getUpdateService();
                    return updateService.checkUpdate(context);
                }

                @Override
                public void doAfterTask(Pair<Boolean, String> result) {
                    if(result.first!=null){
                        if(result.first){
                            DialogUtils.showYesNoDialog(
                                    context,
                                    context.getString(R.string.new_app_available),
                                    result.second,
                                    context.getString(R.string.download),
                                    context.getString(android.R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UpdateService updateService=BeanContainer.getInstance().getUpdateService();
                                    updateService.loadNewVersion(context);
                                    dialog.dismiss();
                                }
                            },new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }else if(userInitiative){
                            DialogUtils.showAlert(context,null,context.getString(R.string.no_new_version),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }else if(userInitiative){
                        DialogUtils.showAlert(context,null,result.second,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void handleError(String error) {

                }

                @Override
                public String getName() {
                    return null;
                }
            };
            if(userInitiative){
                DialogUtils.showLoaderDialog(((BaseActivity)context).getSupportFragmentManager(),task);
            }
            else {
                new LoaderProgressTask<Pair<Boolean,String>>(task,null).execute();
            }
        }
    }

}
