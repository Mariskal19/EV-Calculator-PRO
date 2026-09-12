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

p.write_text(s, encoding="utf-8")
print("Compare theme refresh patch applied.")
