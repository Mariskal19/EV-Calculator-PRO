# Build trigger
# Build trigger for restored Compare header
from pathlib import Path
import re

compare = Path("app/src/main/java/com/evchargecalculator/CompararCochesActivity.java")
s = compare.read_text(encoding="utf-8")

# Rebuild the Compare screen layout as a real overlay stack.  The vehicle
# selector card belongs visually over the bottom of the hero image; using a
# FrameLayout for the hero+card avoids negative margins/translation and avoids
# clipping the card itself.
new_build = r'''    private void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0,0,0,0);
        root.setBackgroundColor(dark ? Color.rgb(7,19,28) : Color.rgb(241,246,251));

        FrameLayout hero = new FrameLayout(this);
        hero.setClipChildren(false);
        hero.setBackgroundColor(Color.TRANSPARENT);

        HeaderBitmapView heroImage = new HeaderBitmapView(this);
        heroImage.setTranslationY(-dp(10));

        hero.addView(heroImage, new FrameLayout.LayoutParams(-1, dp(260)));

        View topFade = new View(this);
        topFade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.argb(200,0,0,0),Color.argb(80,0,0,0),Color.argb(20,0,0,0),Color.argb(0,0,0,0)}));
        hero.addView(topFade, new FrameLayout.LayoutParams(-1,dp(170),Gravity.TOP));

        TextView title = tv("Comparar coches",22,Color.WHITE);
        title.setTypeface(null,Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0));
        FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,dp(56));
        tp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        tp.leftMargin=dp(40);
        tp.rightMargin=dp(40);
        tp.topMargin=dp(4);
        hero.addView(title,tp);

        LinearLayout intro=new LinearLayout(this);
        intro.setOrientation(LinearLayout.VERTICAL);
        intro.setPadding(dp(16),dp(14),dp(16),dp(14));
        intro.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18));
        TextView introTitle=tv("Elige tus vehículos",17,text());
        introTitle.setTypeface(null,Typeface.BOLD);
        intro.addView(introTitle,new LinearLayout.LayoutParams(-1,dp(26)));
        TextView hint=tv("Añade hasta 3 coches para ver sus características y compararlos.",13,sub());
        hint.setPadding(0,dp(2),0,0);
        intro.addView(hint,new LinearLayout.LayoutParams(-1,dp(36)));

        // The card is deliberately laid over the bottom of the hero image.
        // Keep a little more room below it so moving it down does not clip it.
        int cardTop = dp(236);
        int stackHeight = dp(336);
        FrameLayout heroStack = new FrameLayout(this);
        heroStack.setClipChildren(false);
        heroStack.setClipToPadding(false);
        heroStack.setLayoutParams(new LinearLayout.LayoutParams(-1,stackHeight));
        heroStack.addView(hero,new FrameLayout.LayoutParams(-1,dp(260),Gravity.TOP));
        FrameLayout.LayoutParams introFp = new FrameLayout.LayoutParams(-1,-2,Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        introFp.leftMargin=dp(14);
        introFp.rightMargin=dp(14);
        introFp.topMargin=cardTop;
        heroStack.addView(intro,introFp);

        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setClipChildren(false);
        LinearLayout scrollContent=new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setClipChildren(false);
        scrollContent.setClipToPadding(false);
        scrollContent.addView(heroStack,new LinearLayout.LayoutParams(-1,stackHeight));

        LinearLayout content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14),dp(0),dp(14),dp(12));
        content.setClipChildren(false);

        HorizontalScrollView carsScroll=new HorizontalScrollView(this);
        carsScroll.setHorizontalScrollBarEnabled(false);
        carsScroll.setClipToPadding(false);
        carsScroll.setPadding(0,dp(12),0,dp(4));
        carsRow=new LinearLayout(this);
        carsRow.setOrientation(LinearLayout.HORIZONTAL);
        carsRow.setGravity(Gravity.TOP);
        carsScroll.addView(carsRow,new HorizontalScrollView.LayoutParams(-2,-2));
        content.addView(carsScroll,new LinearLayout.LayoutParams(-1,-2));

        TextView section=tv("Características",19,text());
        section.setTypeface(null,Typeface.BOLD);
        section.setPadding(dp(2),dp(12),0,dp(2));
        content.addView(section,new LinearLayout.LayoutParams(-1,dp(42)));
        TextView legend=tv("✦  Mejor valor",12,blue);
        legend.setGravity(Gravity.CENTER_VERTICAL);
        legend.setPadding(dp(4),0,0,dp(4));
        if(selectedIds.size()>=2)content.addView(legend,new LinearLayout.LayoutParams(-1,dp(28)));

        table=new LinearLayout(this);
        table.setOrientation(LinearLayout.VERTICAL);
        table.setPadding(0,dp(2),0,0);
        HorizontalScrollView tableScroll=new HorizontalScrollView(this);
        tableScroll.setHorizontalScrollBarEnabled(false);
        tableScroll.addView(table,new HorizontalScrollView.LayoutParams(-2,-2));
        content.addView(tableScroll,new LinearLayout.LayoutParams(-1,-2));

        summary=new LinearLayout(this);
        summary.setOrientation(LinearLayout.VERTICAL);
        summary.setPadding(0,dp(18),0,dp(8));
        content.addView(summary,new LinearLayout.LayoutParams(-1,-2));

        LinearLayout footer=new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(dp(14),0,dp(14),dp(4));
        String appVersion="1.0.4";
        try{appVersion=getPackageManager().getPackageInfo(getPackageName(),0).versionName;}catch(Exception ignored){}
        if(appVersion.startsWith("v")||appVersion.startsWith("V"))appVersion=appVersion.substring(1);
        TextView foot=tv("Powered by EV Calculator · v"+appVersion,12,sub());
        foot.setGravity(Gravity.CENTER);
        footer.addView(foot,new LinearLayout.LayoutParams(-1,dp(24)));

        if(selectedIds.isEmpty()){
            Space emptyStateSpacer=new Space(this);
            content.addView(emptyStateSpacer,new LinearLayout.LayoutParams(-1,dp(120)));
        }
        Space footerSpacer=new Space(this);
        content.addView(footerSpacer,new LinearLayout.LayoutParams(-1,0,1));
        content.addView(footer,new LinearLayout.LayoutParams(-1,dp(28)));

        scrollContent.addView(content,new LinearLayout.LayoutParams(-1,-2));
        scroll.addView(scrollContent,new ScrollView.LayoutParams(-1,-2));
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
        EdgeToEdgeHelper.apply(this, dark);
    }
'''

pattern = r'    private void build\(\) \{.*?\n    \}\n\n    private List<Vehicle> marketVehicles\(\)'
s, count = re.subn(pattern, new_build + '\n    private List<Vehicle> marketVehicles()', s, count=1, flags=re.S)
if count != 1:
    raise SystemExit(f"Could not replace Compare build() method (matches={count})")

# Keep the main screen title literal, as already established by the project.
principal = Path("app/src/main/java/com/evchargecalculator/PrincipalActivity.java")
s2 = principal.read_text(encoding="utf-8")
old_title = 'TextView title = tv("EV Calculator PRO", 22, Color.WHITE);'
new_title = 'TextView title = new TextView(this); title.setText("EV Calculator PRO"); title.setTextSize(22); title.setTextColor(Color.WHITE);'
if old_title in s2:
    s2 = s2.replace(old_title, new_title, 1)
    principal.write_text(s2, encoding="utf-8")

compare.write_text(s, encoding="utf-8")
print("Compare hero/card rebuilt as a real overlay stack, with the card lowered 10dp and extra empty-state footer spacing.")