package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.betterx.android.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class SupportFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.support_rg)
    RadioGroup radioGroup;
    @Bind(R.id.support_contacts)
    View contactsView;
    @Bind(R.id.support_faq_scrollview)
    View faqView;
    @Bind(R.id.support_faq_container)
    LinearLayout faqContainer;
    @Bind(R.id.support_contact_info)
    TextView contactInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_support, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioGroup.setOnCheckedChangeListener(this);
        prepareFaqList();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        final boolean showFaq = checkedId == R.id.support_faq_rb;
        contactsView.setVisibility(showFaq ? View.GONE : View.VISIBLE);
        faqView.setVisibility(showFaq ? View.VISIBLE : View.GONE);
        contactInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void prepareFaqList() {
        faqContainer.removeAllViews();
        final Map<String, String> faq = getFaqMap();
        for (Map.Entry<String, String> entry : faq.entrySet()){
            addFaqElement(entry.getKey(), entry.getValue());
        }
    }

    private void addFaqElement(String question, String answer) {
        final View faqView = LayoutInflater.from(getActivity()).inflate(R.layout.v_faq, faqContainer, false);
        final TextView questionView = (TextView) faqView.findViewById(R.id.faq_question);
        final TextView answerView = (TextView) faqView.findViewById(R.id.faq_answer);
        questionView.setText(question);
        answerView.setText(answer);

        questionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean answerVisible = answerView.getVisibility() == View.VISIBLE;
                answerView.setVisibility(answerVisible ? View.GONE : View.VISIBLE);
            }
        });

        faqContainer.addView(faqView);
    }

    private Map<String, String> getFaqMap() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("Why should I use BetterX?", "BetterX aims in improving your overall satisfaction using the web. Your get to contribute in academic research anonymously just by installing the app and get a chance to win great prizes. The BetterX app uses state-of-the-art data analysis to learn your web usage patterns so it can speed up and enhance your browsing automatically.");
        map.put("What platforms support BetterX?", "BetterX runs exclusively on Android devices");
        map.put("What prizes can I win?", "We'll be hosting 2 raffle draws during the next 6 months in which we'll be giving away Android tablets. This is our way of saying thank you to our users! Users will receive app notifications before and after each raffle draw announcing prizes and winners.");
        map.put("How can I increase my chances in winning a prize?", "For every friend you invite to use BetterX you get an additional ticket for our raffle draw. The more friends you invite the more chances you have in getting a free tablet!");
        map.put("Where can I get a copy of my data?", "We'll be more than happy to send you a copy of your data at any time. Just use the app and send us a message. We'll respond by sending you a secure link from which you can download a copy of your data.");
        map.put("What is the duration of this project?", "The data collection for BetterX will last for about a year.");
        map.put("Will this application be supported after this project has been completed?", "We'll do our best to keep the app updated and introduce new features after data collection has been completed.");
        map.put("How do I use BetterX?", "All you have to do is just install the app. There nothing else you need to do! :)");
        return map;
    }

}
