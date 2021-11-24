import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

public class PolarChartView extends View {
    /*
    PRIVATE PROPS
     */
    //arrays
    private float[] values;
    private String[] textLabels;
    private Drawable[] drawables;
    //floats
    private float graphSize;
    private float sectionValue;
    private float textLabelDistance;
    private float imageLabelDistance;
    private float textLabelSize;
    private int imageLabelSize;
    private float strokeWidth;
    private float gridWidth;
    private float originMarginX;
    private float originMarginY;
    //booleans
    private boolean isTextLabelDisplay;
    private boolean isImageLabelDisplay;
    //int&colors
    private int primaryColor;
    private int strokeColor;
    private int gridColor;
    private int textLabelColor;
    private int width;
    private int height;
    private int gridUnit;

    Context context;

    /*
    CONSTRUCTORS
     */
    public PolarChartView(Context context) {
        super(context);
        init(context, null);
    }

    public PolarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PolarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /*
    PUBLIC METHODS

    1. setGridUnit : DETERMINA O NÚMERO DE GRIDS

    2. setTextLabelDistance : DETERMINA A DISTÂNCIA ENTRE OS TEXTS LABELS E O GRÁFICO

    3. setValues : DETERMINA OS VALORES MOSTRADOS NO GRAPH

    4. setStrokeWidth : TAMANHO DA LINHA QUE CONTORNA AS REPRESENTAÇÕES DOS VALORES NO GRAPH

    5. setGridWidth : ESPESSURA DA GRID

    6. displayTextLabel : MOSTRAR OS LABELS ESCRITOS OU NÃO?

    7. displayImageLabel : MOSTRAR OS ICONES OU NÃO?

    8. setSize : DETERMINA O TAMANHO DO GRÁFIO

    9. setTextLabelColor : DETERMINA A COLORIZAÇÃO DOS LABELS ESCRITOS

    10. setPrimaryColor : DETERMINA COR DO PREENCHIMENTO DAS REPRESENTAÇÕES DOS VALORES NO GRAPH

    11. setDrawables : DETERMINA OS ICONES QUE SERÃO MOSTRADOS

    12. setStrokeColor : DETERMINA A COR DAS LINHAS QUE CONTORNAM AS REPRESENTAÇÕES DOS VALORES NO GRAPH

    13. setGridColor : DETERMINA A COR DA GRID

    14. setImageLabelDistance : DETERMINA A DISTÂNCIA ENTRE OS IMAGES LABELS E O GRÁFICO

     */
    public void setGridUnit(int gridUnit) {
        this.gridUnit = gridUnit;
        this.invalidate();
    }

    public void setTextLabelDistance(float labelDistance) {
        this.textLabelDistance = labelDistance;
        this.invalidate();
    }

    public void setImageLabelDistance(float labelDistance) {
        this.imageLabelDistance = labelDistance;
        this.invalidate();
    }

    public void setValues(float[] values) {
        this.values = values;
        refreshSections();
        this.invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    public void setGridWidth(float gridWidth) {
        this.gridWidth = gridWidth;
        this.invalidate();
    }

    public void displayTextLabel(boolean display) {
        this.isTextLabelDisplay = display;
        this.invalidate();
    }

    public void displayImageLabel(boolean display) {
        this.isImageLabelDisplay = display;
        this.invalidate();
    }

    public void setSize(int size) {
        this.graphSize = size;
        this.invalidate();
    }

    public void setTextLabelColor(int color) {
        this.textLabelColor = color;
        this.invalidate();
    }

    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        this.invalidate();
    }

    public void setDrawables(Drawable[] drawables) {
        this.drawables = drawables;
        this.invalidate();
    }

    public void setTextLabels(String[] textLabels) {
        this.textLabels = textLabels;
        this.invalidate();
    }

    public void setStrokeColor(int color) {
        this.strokeColor = color;
        this.invalidate();
    }

    public void setGridColor(int color) {
        this.gridColor = color;
        this.invalidate();
    }

    /*
    PRIVATE METHODS
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        width = w;
        height = h;
        originMarginX = w / 2;
        originMarginY = h / 2;
        graphSize = (int) (Math.min(width, height) / 2 * 0.7);

        this.imageLabelDistance = graphSize / 4;
        this.textLabelDistance = graphSize / 10f;
        this.textLabelSize = graphSize / 8f;
        this.imageLabelSize = (int) graphSize / 7;
    }

    private void init(Context context, AttributeSet attrs) {
        this.values = new float[]{80f, 95f, 60f, 60f, 70f, 30f, 50f, 80f, 100f};
        this.textLabels = new String[]{"A", "B", "C", "D", "E", "D"};
        this.gridUnit = 3;
        this.context=context;
        //setDefaultSizes();
        setDefaultConditions();
        setDefaultColors();
        setDefaultWidths();
        refreshSections();
    }

    private void setDefaultSizes() {
        this.graphSize = 300;
        this.originMarginY = 100;
        this.originMarginX = 150;
        this.imageLabelDistance = graphSize /5;
        this.textLabelDistance = graphSize /10;
        this.textLabelSize = graphSize / 8;
        this.imageLabelSize = (int) graphSize / 2;
    }

    private void setDefaultConditions() {
        this.isImageLabelDisplay = false;
        this.isTextLabelDisplay = false;
    }

    private void setDefaultWidths() {
        this.gridWidth = 1f;
        this.strokeWidth = 8f;
    }

    private void setDefaultColors() {
        this.textLabelColor = Color.BLACK;
        this.gridColor = Color.BLACK;
        this.primaryColor = Color.CYAN;
        this.strokeColor = Color.BLUE;
    }

    private void refreshSections() {
        this.sectionValue = 360 / values.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        fillGraph(canvas);
        drawGrid(canvas);
        setStrokeAroundGraph(canvas);

        if (isTextLabelDisplay)
            drawTextLabels(canvas);
        if (isImageLabelDisplay)
            drawImageLabels(canvas, drawables);

    }

    private void drawImageLabels(Canvas canvas, Drawable[] drawables) {

        if (drawables == null) {
            return;
        }

        int temp = 0;

        for (int i = 0; i < values.length; i++) {

            if (i >= values.length)
                return;

            float phi = (temp + sectionValue/2);
            System.out.println("phi"+phi);
            float positionX = getPositionX(phi);
            float positionY = getPositionY(phi);

            drawables[i].setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            drawables[i].setBounds((int) positionX - imageLabelSize, (int) positionY - imageLabelSize, (int) positionX + imageLabelSize, (int) positionY + imageLabelSize);
            drawables[i].draw(canvas);
            temp += (int) sectionValue;

        }
    }

    private void drawTextLabels(Canvas canvas) {

        final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(textLabelColor);
        textPaint.setTextSize(textLabelSize);
        textPaint.setTypeface(Typeface.create(ResourcesCompat.getFont(context, R.font.montserrat), Typeface.BOLD));

        textPaint.setTextAlign(Paint.Align.CENTER);


        int temp = 0;
        String labelText;

        for (int i = 0; i < values.length; i++) {

            if (i >= values.length)
                return;

            if (i >= textLabels.length) {
                labelText = "";
            } else {
                labelText = textLabels[i];
            }
            float phi = (temp + sectionValue/2);

            float positionX = getPositionTextX(phi);
            float positionY = getPositionTextY(phi);


            positionX += getTextPositionXMargin(phi,textPaint.measureText(labelText));
            positionY += getTextPositionYMargin(phi,textLabelSize);

            canvas.drawText(labelText, positionX, positionY, textPaint);
            temp += (int) sectionValue;

        }
    }

    private void fillGraph(Canvas canvas) {
        final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        fillPaint.setColor(primaryColor);


        for (int i = 0; i < values.length; i++) {
            int scaledIntensity = (int) (values[i] * (graphSize / 100));
            float alpha=(values[i]/100f)*0.75f+0.2f;
            fillPaint.setAlpha((int)(0xff*alpha));
            RectF rectf = new RectF(originMarginX - scaledIntensity, originMarginY - scaledIntensity, originMarginX + scaledIntensity, originMarginY + scaledIntensity);
            canvas.drawArc(rectf, (float) i * sectionValue, sectionValue, true, fillPaint);
        }

    }

    private void setStrokeAroundGraph(Canvas canvas) {

        final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setAntiAlias(true);

        for (int i = 0; i < values.length; i++) {
            int scaledIntensity = (int) (values[i] * (graphSize / 100));
            RectF rectf = new RectF(originMarginX - scaledIntensity, originMarginY - scaledIntensity, originMarginX + scaledIntensity, originMarginY + scaledIntensity);
            canvas.drawArc(rectf, (float) i * sectionValue, sectionValue, true, strokePaint);
        }

    }

    private void drawGrid(Canvas canvas) {

        final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(gridWidth);
        gridPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < values.length; i++) {
            for (int j = 1; j <= gridUnit; j++) {
                int radius = (int) (graphSize * ((float) j / gridUnit));
                RectF rectf = new RectF(originMarginX - radius, originMarginY - radius, originMarginX + radius, originMarginY + radius);
                canvas.drawArc(rectf, (float) i * sectionValue, sectionValue, true, gridPaint);
            }
        }

    }


    //USED IN DRAW LABELS
    private float getPositionX(float phi) {
        float phiRadian = (float) (Math.PI * (phi) / 180);
        return originMarginX + (float) Math.cos(phiRadian) * (graphSize+imageLabelDistance);
    }


    //USED IN DRAW LABELS
    private float getPositionY(float phi) {
        float phiRadian = (float) (Math.PI * (phi) / 180);
        return originMarginY + (float) Math.sin(phiRadian) * (graphSize+imageLabelDistance);
    }

    //USED IN DRAW LABELS
    private float getPositionTextX(float phi) {
        float phiRadian = (float) (Math.PI * (phi) / 180);
        return originMarginX + (float) Math.cos(phiRadian) * (graphSize+textLabelDistance);
    }


    //USED IN DRAW LABELS
    private float getPositionTextY(float phi) {
        float phiRadian = (float) (Math.PI * (phi) / 180);
        return originMarginY + (float) Math.sin(phiRadian) * (graphSize+textLabelDistance);
    }

    //USED IN DRAW LABELS
    private float getTextPositionXMargin(float phi,float w) {

        float phiRadian = (float) (Math.PI * (phi) / 180);
        return (float)(0.5*w*Math.cos(phiRadian));
    }

    private float getTextPositionYMargin(float phi,float h) {

        float phiRadian = (float) (Math.PI * (phi) / 180);
        return (float)(0.5*h*Math.sin(phiRadian));
    }

}

enum GRAPH_SECTION {
    FIRST, //90 =< x < 0
    SECOND, //180 =< x < 0
    THIRD, //270 =< x < 180
    FOURTH //360 =< x < 270
}
