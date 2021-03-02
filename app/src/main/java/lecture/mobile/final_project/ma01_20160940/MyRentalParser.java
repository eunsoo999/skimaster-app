package lecture.mobile.final_project.ma01_20160940;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;

//렌탈샵api의 파서
public class MyRentalParser {
    private enum TagType { NONE, CAT3, TITLE, ADDRESS, TEL, CONTENTID, OVERVIEW};

    private final static String ITEM_TAG = "item";
    private final static String CAT3_TAG = "cat3";
    private final static String TITLE_TAG = "title"; //렌탈샵이름
    private final static String ADDRESS_TAG = "addr1"; //렌탈샵 주소
    private final static String TEL_TAG = "tel"; //렌탈샵 전화번호
    private final static String OVERVIEW_TAG = "overview";
    private final static String CONTENTID_TAG = "contentid";


    private XmlPullParser parser;

    public MyRentalParser() {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<RentalItem> parse(String xml) {

        ArrayList<RentalItem> resultList = new ArrayList();
        RentalItem dbo = null;
        int confirm = 0; // 근처에있는 장소중에 렌탈샵이면 0, 그외의 장소면 1

        TagType tagType = TagType.NONE;

        try {
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            dbo = new RentalItem();
                        } else if (parser.getName().equals(ADDRESS_TAG)) {
                            tagType = TagType.ADDRESS;
                        } else if (parser.getName().equals(TITLE_TAG)) {
                            tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TEL_TAG)) {
                            tagType = TagType.TEL;
                        } else if (parser.getName().equals(CAT3_TAG)) {
                            tagType = TagType.CAT3;
                        } else if (parser.getName().equals(CONTENTID_TAG)) {
                            tagType = TagType.CONTENTID;
                        } else if (parser.getName().equals(OVERVIEW_TAG)) {
                            tagType = TagType.OVERVIEW;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            if(confirm == 0)
                                resultList.add(dbo);
                            confirm = 0;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case ADDRESS:
                                dbo.setAddress(parser.getText());
                                break;
                            case TITLE:
                                dbo.setTitle(parser.getText());
                                break;
                            case TEL:
                                dbo.setTel(parser.getText());
                                break;
                            case CAT3:
                                if (!parser.getText().equals("A03022600"))
                                    confirm = 1;
                                break;
                            case CONTENTID:
                                dbo.setContentid(parser.getText());
                                break;
                            case OVERVIEW:
                                dbo.setOverview(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}

