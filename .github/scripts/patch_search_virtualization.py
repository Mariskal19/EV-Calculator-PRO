from pathlib import Path
import re

p = Path("app/src/main/java/com/evchargecalculator/CompararCochesActivity.java")
s = p.read_text(encoding="utf-8")

print("Search virtualization already integrated; no search rewrite needed.")

# Keep the existing header image and apply a dark overlay instead of referencing
# a nonexistent dark drawable.
old_bad = 'heroImage.setImageResource(R.drawable.cabecera_tema_oscuro);'
old_light = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro);'
new_header = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro); heroImage.setColorFilter(dark ? 0x88000000 : Color.TRANSPARENT, android.graphics.PorterDuff.Mode.SRC_OVER);'
if old_bad in s:
    s = s.replace(old_bad, new_header, 1)
elif old_light in s and 'heroImage.setColorFilter' not in s:
    s = s.replace(old_light, new_header, 1)

# Rebuild the complete Compare screen after a theme change.
old_resume = '''    @Override
    protected void onResume() {
        super.onResume();
        LanguageManager.applyStored(this);
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean newDark = p.contains("dark_theme") ? p.getBoolean("dark_theme", false) : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
        boolean themeChanged = dark != newDark;
        dark = newDark;
        if (!vehicles.isEmpty()) {
            loadSelection();
            if (themeChanged) build();
            rebuild();
        }
    }
'''
new_resume = '''    @Override
    protected void onResume() {
        super.onResume();
        LanguageManager.applyStored(this);
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean newDark = p.contains("dark_theme") ? p.getBoolean("dark_theme", false) : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
        boolean themeChanged = dark != newDark;
        dark = newDark;
        if (!vehicles.isEmpty()) {
            loadSelection();
            if (themeChanged) build();
            rebuild();
        }
    }
'''
if old_resume in s:
    s = s.replace(old_resume, new_resume, 1)

old_callback = 'dark=value;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean("dark_theme",dark).apply();build();loadSelection();rebuild();'
new_callback = 'dark=value;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean("dark_theme",dark).apply();loadSelection();build();rebuild();'
if old_callback in s:
    s = s.replace(old_callback, new_callback, 1)

# Match the standard back button used by the other screens.
old_back = 'TextView back = tv("‹",40,Color.WHITE); back.setGravity(Gravity.CENTER); back.setTypeface(null,Typeface.BOLD); back.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0)); back.setOnClickListener(v->finish()); FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.START); bp.leftMargin=dp(14); bp.topMargin=dp(12); hero.addView(back,bp);'
new_back = 'TextView back = tv("←",30,Color.WHITE); back.setGravity(Gravity.CENTER); back.setIncludeFontPadding(false); back.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); back.setBackgroundColor(Color.TRANSPARENT); back.setPadding(0,0,0,0); back.setTranslationY(-dp(4)); back.setOnClickListener(v->finish()); FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.START); bp.leftMargin=dp(14); bp.topMargin=dp(12); hero.addView(back,bp);'
if old_back in s:
    s = s.replace(old_back, new_back, 1)

# The Compare header must use the same top origin as the other screens.
# Do not add the status-bar height as content padding: the other screens draw
# their header from the top edge, with the status-bar area overlaying the image.
old_root = 'LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); int statusBarHeight = getResources().getIdentifier("status_bar_height", "dimen", "android") > 0 ? getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")) : 0; root.setPadding(0,statusBarHeight,0,0); root.setBackgroundColor(dark ? Color.rgb(7,19,28) : Color.rgb(241,246,251));'
new_root = 'LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(0,0,0,0); root.setBackgroundColor(dark ? Color.rgb(7,19,28) : Color.rgb(241,246,251));'
if old_root in s:
    s = s.replace(old_root, new_root, 1)
else:
    s = re.sub(r'LinearLayout root = new LinearLayout\(this\); root\.setOrientation\(LinearLayout\.VERTICAL\); int statusBarHeight = .*?; root\.setBackgroundColor\(', 'LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(0,0,0,0); root.setBackgroundColor(', s, count=1)

# IMPORTANT: the entire hero must be inside the same ScrollView as the page.
# Never leave hero attached to root. A View cannot have two parents.
s = s.replace('root.addView(hero);', '', 1)

# Do this independently from the end replacement so a partial previous patch
# can never leave an undefined scrollContent variable.
if 'LinearLayout scrollContent=new LinearLayout(this);' not in s:
    pattern = r'ScrollView scroll=new ScrollView\(this\); scroll\.setFillViewport\(true\); scroll\.setClipToPadding\(false\); LinearLayout content=new LinearLayout\(this\);'
    replacement = 'ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setClipToPadding(false); LinearLayout scrollContent=new LinearLayout(this); scrollContent.setOrientation(LinearLayout.VERTICAL); scrollContent.setClipChildren(false); LinearLayout content=new LinearLayout(this);'
    s, count = re.subn(pattern, replacement, s, count=1)
    print("Compare scroll container declaration matches:", count)

# Put the hero into the scroll container. Remove any existing parent first as a
# defensive guard so this can never throw IllegalStateException at runtime.
hero_add = 'scrollContent.addView(hero,new LinearLayout.LayoutParams(-1,dp(260)));'
hero_add_safe = 'android.view.ViewParent heroParent=hero.getParent(); if(heroParent instanceof android.view.ViewGroup)((android.view.ViewGroup)heroParent).removeView(hero); scrollContent.addView(hero,new LinearLayout.LayoutParams(-1,dp(260)));'
if hero_add_safe not in s:
    s = s.replace(hero_add, hero_add_safe, 1)

# Put content inside scrollContent, then scrollContent inside the ScrollView.
if 'scrollContent.addView(content,new LinearLayout.LayoutParams(-1,-2)); scroll.addView(scrollContent' not in s:
    old_end = 'scroll.addView(content,new ScrollView.LayoutParams(-1,-1)); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));'
    new_end = 'scrollContent.addView(content,new LinearLayout.LayoutParams(-1,-2)); scroll.addView(scrollContent,new ScrollView.LayoutParams(-1,-2)); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));'
    if old_end in s:
        s = s.replace(old_end, new_end, 1)
    else:
        pattern = r'scroll\.addView\(content,new ScrollView\.LayoutParams\(-1,-1\)\); root\.addView\(scroll,new LinearLayout\.LayoutParams\(-1,0,1\)\);'
        s, count = re.subn(pattern, new_end, s, count=1)
        print("Compare scroll container end matches:", count)

# First Compare card overlaps the bottom of the hero by 26dp, as on the other
# screens, eliminating the visible strip under the image.
if 'introLp.topMargin = -dp(26);' not in s:
    pattern = r'content\.addView\(intro\);'
    replacement = 'LinearLayout.LayoutParams introLp = new LinearLayout.LayoutParams(-1, -2); introLp.topMargin = -dp(26); intro.setLayoutParams(introLp); content.addView(intro);'
    s, count = re.subn(pattern, replacement, s, count=1)
    print("Compare header overlap patch matches:", count)

p.write_text(s, encoding="utf-8")
print("Compare theme refresh, back-button alignment and scrolling header patches applied.")

# The product title on the main screen is a brand name and must never be translated.
p2 = Path("app/src/main/java/com/evchargecalculator/PrincipalActivity.java")
s2 = p2.read_text(encoding="utf-8")
old_title = 'TextView title = tv("EV Calculator PRO", 22, Color.WHITE);'
new_title = 'TextView title = new TextView(this); title.setText("EV Calculator PRO"); title.setTextSize(22); title.setTextColor(Color.WHITE);'
if old_title in s2:
    s2 = s2.replace(old_title, new_title, 1)
p2.write_text(s2, encoding="utf-8")
print("Main title kept as EV Calculator PRO in every language.")
