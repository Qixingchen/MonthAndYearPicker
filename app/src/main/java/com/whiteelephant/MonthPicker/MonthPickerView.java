package com.whiteelephant.MonthPicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prem.firstpitch.*;

import java.util.Calendar;
import java.util.HashMap;

class MonthPickerView extends FrameLayout {

    private YearPickerView _yearView;
    private ListView _monthList;
    private int _minYear, _maxYear;
    private MonthViewAdapter _monthViewAdapter;
    private TextView _month, _year, _title;
    private Context _context;
    private String _headerTitle;
    private int _headerFontColorSelected, _headerFontColorNormal;
    private boolean _showMonthOnly;
    private int _selectedMonth, _selectedYear;
    private MonthPickerDialog.OnYearChangedListener _onYearChanged;
    private MonthPickerDialog.OnMonthChangedListener _onMonthChanged;
    private OnDateSet _onDateSet;
    private OnCancel _onCancel;

    public MonthPickerView(Context context) {
        this(context, null);
        _context = context;
    }

    public MonthPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        _context = context;
    }

    public MonthPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.month_picker_view, this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.monthPickerDialog, defStyleAttr, 0);

        int headerBgColor = a.getColor(R.styleable.monthPickerDialog_headerBgColor, 0);
        _headerFontColorNormal = a.getColor(R.styleable.monthPickerDialog_headerFontColorNormal, 0);
        _headerFontColorSelected = a.getColor(R.styleable.monthPickerDialog_headerFontColorSelected, 0);
        int monthBgColor = a.getColor(R.styleable.monthPickerDialog_monthBgColor, 0);
        int monthBgSelectedColor = a.getColor(R.styleable.monthPickerDialog_monthBgSelectedColor, 0);
        int monthFontColorNormal = a.getColor(R.styleable.monthPickerDialog_monthFontColorNormal, 0);
        int monthFontColorSelected = a.getColor(R.styleable.monthPickerDialog_monthFontColorSelected, 0);
        int monthFontColorDisabled = a.getColor(R.styleable.monthPickerDialog_monthFontColorDisabled, 0);
        int headerTitleColor = a.getColor(R.styleable.monthPickerDialog_headerTitleColor, 0);

        if (monthFontColorNormal == 0) {
            monthFontColorNormal = getResources().getColor(R.color.fontBlackEnable);
        }
        if (monthFontColorSelected == 0) {
            monthFontColorSelected = getResources().getColor(R.color.fontWhiteEnable);
        }
        if (monthFontColorDisabled == 0) {
            monthFontColorDisabled = getResources().getColor(R.color.fontBlackDisable);
        }
        if (_headerFontColorNormal == 0) {
            _headerFontColorNormal = getResources().getColor(R.color.fontWhiteDisable);
        }
        if (_headerFontColorSelected == 0) {
            _headerFontColorSelected = getResources().getColor(R.color.fontWhiteEnable);
        }
        if (headerTitleColor == 0) {
            headerTitleColor = getResources().getColor(R.color.fontWhiteEnable);
        }
        if (monthBgColor == 0) {
            monthBgColor = getResources().getColor(R.color.fontWhiteEnable);
        }

        if (headerBgColor == 0) {
            int checkExistence = context.getResources().getIdentifier("colorAccent", "color", context.getPackageName());
            if (checkExistence != 0) {
                headerBgColor = context.getResources().getColor(R.color.colorAccent);
            } else {
                headerBgColor = getResources().getColor(R.color.colorAccent);
            }
        }

        if (monthBgSelectedColor == 0) {
            int checkExistence = context.getResources().getIdentifier("colorAccent", "color", context.getPackageName());
            if (checkExistence != 0) {
                monthBgSelectedColor = context.getResources().getColor(R.color.colorAccent);
            } else {
                monthBgSelectedColor = getResources().getColor(R.color.colorAccent);
            }
        }


        HashMap<String, Integer> map = new HashMap();
        map.put("monthBgColor", monthBgColor);
        map.put("monthBgSelectedColor", monthBgSelectedColor);
        map.put("monthFontColorNormal", monthFontColorNormal);
        map.put("monthFontColorSelected", monthFontColorSelected);
        map.put("monthFontColorDisabled", monthFontColorDisabled);
        Log.d("----------------", " headerBgColor" + headerBgColor + " headerFontColorNormal" + _headerFontColorNormal + " headerFontColorSelected : "
                + _headerFontColorSelected + " headerTitleColor : " + headerTitleColor + " monthBgColor:  " + monthBgColor + " monthBgSelectedColor:  "
                + monthBgSelectedColor + " monthFontColorDisabled : " + monthFontColorDisabled + " monthFontColorNormal : "
                + monthFontColorNormal + " monthFontColorSelected: " + monthFontColorSelected);

        a.recycle();

        _monthList = (ListView) findViewById(R.id.listview);
        _yearView = (YearPickerView) findViewById(R.id.yearView);
        _month = (TextView) findViewById(R.id.month);
        _year = (TextView) findViewById(R.id.year);
        _title = (TextView) findViewById(R.id.title);
        RelativeLayout _pickerBg = (RelativeLayout) findViewById(R.id.picker_view);
        LinearLayout _header = (LinearLayout) findViewById(R.id.header);
        Button ok = (Button) findViewById(R.id.ok_action);
        Button cancel = (Button) findViewById(R.id.cancel_action);


        ok.setTextColor(headerBgColor);
        cancel.setTextColor(headerBgColor);
        _month.setTextColor(_headerFontColorSelected);
        _year.setTextColor(_headerFontColorNormal);
        _title.setTextColor(headerTitleColor);
        _header.setBackgroundColor(headerBgColor);
        _pickerBg.setBackgroundColor(monthBgColor);

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                _onDateSet.onDateSet();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                _onCancel.onCancel();
            }
        });
        _monthViewAdapter = new MonthViewAdapter(context);
        _monthViewAdapter.setColors(map);
        _monthViewAdapter.setOnDaySelectedListener(new MonthViewAdapter.OnDaySelectedListener() {
            @Override
            public void onDaySelected(MonthViewAdapter view, int selectedMonth) {
                Log.d("----------------", "TestView selected month = " + selectedMonth);
                MonthPickerView.this._selectedMonth = selectedMonth;
                _month.setText(_context.getResources().getStringArray(R.array.months)[selectedMonth - 1]);
                if (!_showMonthOnly) {
                    _monthList.setVisibility(View.GONE);
                    _yearView.setVisibility(View.VISIBLE);
                    _month.setTextColor(_headerFontColorNormal);
                    _year.setTextColor(_headerFontColorSelected);
                }
                if (_onMonthChanged != null) {
                    _onMonthChanged.onMonthChanged(selectedMonth);
                }
            }
        });
        _monthList.setAdapter(_monthViewAdapter);

        _yearView.setRange(_minYear, _maxYear);
        _yearView.setColors(map);
        _yearView.setYear(Calendar.getInstance().get(Calendar.YEAR));
        _yearView.setOnYearSelectedListener(new YearPickerView.OnYearSelectedListener() {
            @Override
            public void onYearChanged(YearPickerView view, int selectedYear) {
                Log.d("----------------", "selected year = " + selectedYear);
                MonthPickerView.this._selectedYear = selectedYear;
                _year.setText("" + selectedYear);
                _year.setTextColor(_headerFontColorSelected);
                _month.setTextColor(_headerFontColorNormal);
                if (_onYearChanged != null) {
                    _onYearChanged.onYearChanged(selectedYear);
                }
            }
        });
        _month.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_monthList.getVisibility() == GONE) {
                    _yearView.setVisibility(GONE);
                    _monthList.setVisibility(VISIBLE);
                    _year.setTextColor(_headerFontColorNormal);
                    _month.setTextColor(_headerFontColorSelected);
                }
            }
        });
        _year.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                if (_yearView.getVisibility() == GONE) {
                    _monthList.setVisibility(GONE);
                    _yearView.setVisibility(VISIBLE);
                    _year.setTextColor(_headerFontColorSelected);
                    _month.setTextColor(_headerFontColorNormal);
                }
            }
        });
    }

    protected void init(int year, int month) {
        _selectedYear = year;
        _selectedMonth = month;
    }

    protected void setMaxMonth(int maxMonth) {
        if (maxMonth <= Calendar.DECEMBER && maxMonth >= Calendar.JANUARY) {
            _monthViewAdapter.setMaxMonth(maxMonth);
        } else {
            throw new IllegalArgumentException("Month out of range please send months between Calendar.JANUARY, Calendar.DECEMBER");
        }
    }


    protected void setMinMonth(int minMonth) {
        if (minMonth >= Calendar.JANUARY && minMonth <= Calendar.DECEMBER) {
            _monthViewAdapter.setMinMonth(minMonth);
        } else {
            throw new IllegalArgumentException("Month out of range please send months between Calendar.JANUARY, Calendar.DECEMBER");
        }
    }

    protected void setMinYear(int minYear) {
        _yearView.setMinYear(minYear);
    }

    protected void setMaxYear(int maxYear) {
        _yearView.setMaxYear(maxYear);
    }

    protected void showMonthOnly() {
        _showMonthOnly = true;
        _year.setVisibility(GONE);
    }

    protected void showYearOnly() {
        _monthList.setVisibility(View.GONE);
        _yearView.setVisibility(VISIBLE);

        _month.setVisibility(GONE);
        _year.setTextColor(_headerFontColorSelected);
    }

    protected void setActivatedMonth(int activatedMonth) {
        if (activatedMonth >= Calendar.JANUARY && activatedMonth <= Calendar.DECEMBER) {
            _monthViewAdapter.setActivatedMonth(activatedMonth);
        } else {
            throw new IllegalArgumentException("Month out of range please send months between Calendar.JANUARY, Calendar.DECEMBER");
        }

    }

    protected void setActivatedYear(int activatedYear) {
        _yearView.setActivatedYear(activatedYear);
    }

    protected void setMonthRange(int minMonth, int maxMonth) {
        if (minMonth < maxMonth) {
            setMinMonth(minMonth);
            setMaxYear(maxMonth);
        } else {
            throw new IllegalArgumentException("maximum month is less then minimum month");
        }
    }

    protected void setYearRange(int minYear, int maxYear) {
        if (minYear < maxYear) {
            setMinYear(minYear);
            setMaxYear(maxYear);
        } else {
            throw new IllegalArgumentException("maximum year is less then minimum year");
        }
    }

    protected void setMonthYearRange(int minMonth, int maxMonth, int minYear, int maxYear) {
        setMonthRange(minMonth, maxMonth);
        setYearRange(minYear, maxYear);
    }

    protected void setTitle(String dialogTitle) {
        if (dialogTitle != null) {
            this._headerTitle = dialogTitle;
            _title.setVisibility(VISIBLE);
        } else {
            _title.setVisibility(GONE);
        }
    }

    protected int getMonth() {
        return _selectedMonth;
    }

    protected int getYear() {
        return _selectedYear;
    }

    protected void setOnMonthChangedListener(MonthPickerDialog.OnMonthChangedListener onMonthChangedListener) {
        if (onMonthChangedListener != null) {
            this._onMonthChanged = onMonthChangedListener;
        }
    }

    protected void setOnYearChangedListener(MonthPickerDialog.OnYearChangedListener onYearChangedListener) {
        if (onYearChangedListener != null) {
            this._onYearChanged = onYearChangedListener;
        }
    }

    public void setOnDateListener(OnDateSet onDateSet) {
        this._onDateSet = onDateSet;
    }

    public void setOnCancelListener(OnCancel onCancel) {
        this._onCancel = onCancel;
    }


    public interface OnDateSet{
        void onDateSet();
    }
    public interface OnCancel{
        void onCancel();
    }
}