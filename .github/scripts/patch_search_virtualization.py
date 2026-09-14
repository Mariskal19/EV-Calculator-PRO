from pathlib import Path

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

# Put the "Elige tus vehículos" card outside the ScrollView so it can overlap
# the bottom of the header like the cards on the other screens. The ScrollView
# starts 12dp below the card, which preserves the existing vehicle-selector
# position exactly while the card itself starts 26dp above the header bottom.
old_layout = '''        root.addView(hero);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setClipToPadding(false); LinearLayout.LayoutParams scrollLp=new LinearLayout.LayoutParams(-1,0,1); scrollLp.topMargin=-dp(26); LinearLayout content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(14),dp(14),dp(14),dp(12));
        LinearLayout intro=new LinearLayout(this); intro.setOrientation(LinearLayout.VERTICAL); intro.setPadding(dp(16),dp(14),dp(16),dp(14)); intro.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18)); TextView introTitle=tv("Elige tus vehículos",17,text()); introTitle.setTypeface(null,Typeface.BOLD); intro.addView(introTitle,new LinearLayout.LayoutParams(-1,dp(26))); TextView hint=tv("Añade hasta 3 coches para ver sus características y compararlos.",13,sub()); hint.setPadding(0,dp(2),0,0); intro.addView(hint,new LinearLayout.LayoutParams(-1,dp(36))); LinearLayout.LayoutParams introLp=new LinearLayout.LayoutParams(-1,-2); introLp.topMargin=-dp(14); content.addView(intro,introLp);
        HorizontalScrollView carsScroll='''
new_layout = '''        root.addView(hero);
        LinearLayout intro=new LinearLayout(this); intro.setOrientation(LinearLayout.VERTICAL); intro.setPadding(dp(16),dp(14),dp(16),dp(14)); intro.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18)); TextView introTitle=tv("Elige tus vehículos",17,text()); introTitle.setTypeface(null,Typeface.BOLD); intro.addView(introTitle,new LinearLayout.LayoutParams(-1,dp(26))); TextView hint=tv("Añade hasta 3 coches para ver sus características y compararlos.",13,sub()); hint.setPadding(0,dp(2),0,0); intro.addView(hint,new LinearLayout.LayoutParams(-1,dp(36))); LinearLayout.LayoutParams introLp=new LinearLayout.LayoutParams(-1,-2); introLp.topMargin=-dp(26); root.addView(intro,introLp);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setClipToPadding(false); LinearLayout.LayoutParams scrollLp=new LinearLayout.LayoutParams(-1,0,1); scrollLp.topMargin=dp(12); LinearLayout content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(14),dp(14),dp(14),dp(12));
        HorizontalScrollView carsScroll='''
if old_layout in s:
    s = s.replace(old_layout, new_layout, 1)
elif 'introLp.topMargin=-dp(26); root.addView(intro,introLp);' not in s:
    raise SystemExit('Expected compare header layout block not found')

p.write_text(s, encoding="utf-8")
print("Compare theme and header layout patches applied.")
