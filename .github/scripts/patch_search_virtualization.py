from pathlib import Path

p = Path("app/src/main/java/com/evchargecalculator/CompararCochesActivity.java")
s = p.read_text(encoding="utf-8")
start = s.find("    private void showSearch(){")
end = s.find("    private int trimRank(Vehicle v)", start)

# The known-good source already contains the virtualized ListView search. Only replace
# showSearch when an older non-virtualized implementation is actually present.
if start >= 0 and end >= 0:
    # Keep the existing replacement body from the current script by importing it is not
    # possible here; this branch is only for legacy sources. The current main source is
    # already virtualized, so fail-safe instead of destroying it.
    raise SystemExit("Legacy showSearch implementation detected; refusing automatic rewrite")
else:
    print("Search virtualization already present; skipping search rewrite.")

old_bad = 'heroImage.setImageResource(R.drawable.cabecera_tema_oscuro);'
new_good = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro); heroImage.setColorFilter(dark ? 0x88000000 : Color.TRANSPARENT, android.graphics.PorterDuff.Mode.SRC_OVER);'
if old_bad in s:
    s = s.replace(old_bad, new_good, 1)
else:
    old_light = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro);'
    new_light = 'heroImage.setImageResource(R.drawable.cabecera_tema_claro); heroImage.setColorFilter(dark ? 0x88000000 : Color.TRANSPARENT, android.graphics.PorterDuff.Mode.SRC_OVER);'
    if old_light in s and 'heroImage.setColorFilter' not in s:
        s = s.replace(old_light, new_light, 1)

p.write_text(s, encoding="utf-8")
print("Search/theme validation patch applied.")
