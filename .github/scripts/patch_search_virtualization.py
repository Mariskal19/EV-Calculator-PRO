from pathlib import Path
import re

p = Path("app/src/main/java/com/evchargecalculator/CompararCochesActivity.java")
s = p.read_text(encoding="utf-8")

# Search virtualization is already integrated in main. Keep this build-time patch
# deliberately idempotent: never rewrite showSearch(), so the working ListView
# implementation cannot be destroyed by a future build.
print("Search virtualization already integrated; no search rewrite needed.")

# The project only contains cabecera_tema_claro. Dark mode uses the same existing
# drawable with a dark ColorFilter, avoiding a nonexistent resource.
old_bad = 'heroImage.setImageResource(R.drawable.cabecera_tema_oscuro);'
old_light = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro);'
new_header = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro); heroImage.setColorFilter(dark ? 0x88000000 : Color.TRANSPARENT, android.graphics.PorterDuff.Mode.SRC_OVER);'

if old_bad in s:
    s = s.replace(old_bad, new_header, 1)
elif old_light in s and 'heroImage.setColorFilter' not in s:
    s = s.replace(old_light, new_header, 1)

# When returning from Configuration, a theme change must rebuild the complete
# comparison screen, not only its dynamic rows. Otherwise static views such as
# the intro card and section headers retain the previous theme colors.
old_resume = '''    @Override
    protected void onResume() {
        super.onResume();
        LanguageManager.applyStored(this);
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean newDark = p.contains("dark_theme") ? p.getBoolean("dark_theme", false) : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
        dark = newDark;
        if (!vehicles.isEmpty()) {
            loadSelection();
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

# If the theme is changed from Comparar's own menu, restore the selection before
# rebuilding the static hierarchy so elements created by build() see the new
# selected-state and theme from the start.
old_callback = 'dark=value;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean("dark_theme",dark).apply();build();loadSelection();rebuild();'
new_callback = 'dark=value;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean("dark_theme",dark).apply();loadSelection();build();rebuild();'
if old_callback in s:
    s = s.replace(old_callback, new_callback, 1)

# Keep the Comparar coches back button identical to the standard back button
# used by the other app screens: arrow, size, padding and vertical alignment.
old_back = 'TextView back = tv("‹",40,Color.WHITE); back.setGravity(Gravity.CENTER); back.setTypeface(null,Typeface.BOLD); back.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0)); back.setOnClickListener(v->finish()); FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.START); bp.leftMargin=dp(14); bp.topMargin=dp(12); hero.addView(back,bp);'
new_back = 'TextView back = tv("←",30,Color.WHITE); back.setGravity(Gravity.CENTER); back.setIncludeFontPadding(false); back.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); back.setBackgroundColor(Color.TRANSPARENT); back.setPadding(0,0,0,0); back.setTranslationY(-dp(4)); back.setOnClickListener(v->finish()); FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.START); bp.leftMargin=dp(14); bp.topMargin=dp(12); hero.addView(back,bp);'
if old_back in s:
    s = s.replace(old_back, new_back, 1)

# Match the other app screens: the first Compare card overlaps the bottom of
# the hero by 26dp. This removes the visible blank strip that otherwise belongs
# to the header area while leaving the header image itself unchanged.
if 'compare header gap' not in s:
    pattern = r'content\\.addView\\(intro(?:\\s*,[^;]*)?\\);'
    replacement = 'LinearLayout.LayoutParams introLp = new LinearLayout.LayoutParams(-1, -2); introLp.topMargin = -dp(26); intro.setLayoutParams(introLp); content.addView(intro);'
    s, count = re.subn(pattern, replacement, s, count=1)
    print("Compare header overlap patch matches:", count)

p.write_text(s, encoding="utf-8")
print("Compare theme refresh, back-button alignment and header-gap patches applied.")

# The product title on the main screen is a brand name and must never be translated.
p2 = Path("app/src/main/java/com/evchargecalculator/PrincipalActivity.java")
s2 = p2.read_text(encoding="utf-8")
old_title = 'TextView title = tv("EV Calculator PRO", 22, Color.WHITE);'
new_title = 'TextView title = new TextView(this); title.setText("EV Calculator PRO"); title.setTextSize(22); title.setTextColor(Color.WHITE);'
if old_title in s2:
    s2 = s2.replace(old_title, new_title, 1)
p2.write_text(s2, encoding="utf-8")
print("Main title kept as EV Calculator PRO in every language.")
