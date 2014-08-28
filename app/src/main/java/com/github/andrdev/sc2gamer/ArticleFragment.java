package com.github.andrdev.sc2gamer;


import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

/**Fragment that shows article, after click on a row from NewsListFragment.
 *
 * */
public class ArticleFragment extends WebViewFragment {
    private String mArticleText;
    private String mLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mLink = getActivity().getIntent().getStringExtra(NewsTable.LINK);
        if (mLink == null) {
            mLink = getArguments().getString("Link");
        }
    }

    @Override
    String getPage() {
        return JsoupHelper.GAME_SITE+mLink;
    }
}
