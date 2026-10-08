            if(distinctBrandCount(competitors)>=MIN_DISTINCT_BRANDS)break;
            competitors=buildCompetitionCandidates(reference,priceLimit,COMPETITOR_POOL,referenceYear,minYear,8.0,5.0,8.0,false);
        }
        double[] relaxedDimensionPriceFallback={50.0,60.0,70.0};
        for(double priceLimit:relaxedDimensionPriceFallback){
            if(distinctBrandCount(competitors)>=MIN_DISTINCT_BRANDS)break;
            competitors=buildCompetitionCandidates(reference,priceLimit,COMPETITOR_POOL,referenceYear,minYear,12.0,8.0,12.0,false);
        }
        if(distinctBrandCount(competitors)<MIN_DISTINCT_BRANDS)
            competitors=buildCompetitionCandidates(reference,70.0,COMPETITOR_POOL,referenceYear,minYear,15.0,10.0,15.0,false);

        // FASE 2: de los 100 competidores, seleccionar primero los 10
        // que compiten mejor con el coche de referencia.
        // buildCompetitionCandidates() ya devuelve el pool ordenado por
        // competencia pura, así que aquí no se mezcla todavía con las
        // características.
        Collections.sort(competitors,(x,y)->{
            int c=Double.compare(x.competitionScore,y.competitionScore);
            if(c!=0)return c;
            c=Integer.compare(Math.abs(x.v.year-referenceYear),Math.abs(y.v.year-referenceYear));
            if(c!=0)return c;
            return Integer.compare(y.v.year,x.v.year);
        });

        // Una sola representación por marca: primero se eligen las 10 marcas
        // con mejor competencia. Ningún criterio técnico puede excluir aquí
        // a un competidor real.
        Map<String,Scored> bestByMake=new LinkedHashMap<>();
        for(Scored s:competitors){
            String make=s.v.make==null?"":s.v.make.trim().toLowerCase(Locale.ROOT);
            if(make.isEmpty())continue;
            if(!bestByMake.containsKey(make))bestByMake.put(make,s);
            if(bestByMake.size()>=FINAL_TOP)break;
        }
        List<Scored> uniqueBrands=new ArrayList<>(bestByMake.values());

        // FASE 3: ahora sí, ordenar SOLO esos 10 por características.
        // La competencia ya ha decidido quién entra; las características
        // deciden el orden final.
        Collections.sort(uniqueBrands,(a,b)->{
            int c=Double.compare(a.technicalScore,b.technicalScore);
            if(c!=0)return c;
            c=Double.compare(a.competitionScore,b.competitionScore);
            if(c!=0)return c;
            c=Integer.compare(Math.abs(a.v.year-referenceYear),Math.abs(b.v.year-referenceYear));
            if(c!=0)return c;
            return Integer.compare(b.v.year,a.v.year);
        });

        int n=Math.min(FINAL_TOP,uniqueBrands.size());
        for(int i=0;i<n;i++)addSimilarCard(uniqueBrands.get(i),i+1);
        if(n==0)results.addView(tv("No hay suficientes opciones similares con estos filtros.",13,sub()));
    }

    /** FASE 1: competencia pura. La similitud técnica no decide quién entra. */
    private List<Scored> buildCompetitionCandidates(Vehicle reference,double maxPricePercent,int topPool,
            int referenceYear,int minYear,double lengthTolerance,double widthTolerance,double heightTolerance,boolean requireSegment){
        List<Scored> all=new ArrayList<>();
        for(Vehicle v:vehicles){
            if(v==reference)continue;
            if(reference.make!=null&&v.make!=null&&reference.make.trim().equalsIgnoreCase(v.make.trim()))continue;