package lecture.mobile.final_project.ma01_20160940;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
//이미지이름바꿔야함
//스키장api의 파서
public class MyXmlParser {
    private enum TagType { NONE, TITLE, ADDRESS, TEL, MAPX, MAPY, CONTENTID, OVERVIEW};

    private final static String ITEM_TAG = "item";
    private final static String TITLE_TAG = "title"; //스키장이름
    private final static String ADDRESS_TAG = "addr1"; //스키장 주소
    private final static String TEL_TAG = "tel"; //스키장 전화번호
    private final static String MAPX_TAG = "mapx"; //스키장위치 위도
    private final static String MAPY_TAG = "mapy"; //스키장위치 경도
    private final static String CONTENTID_TAG = "contentid";
    private final static String OVERVIEW_TAG = "overview";
    private XmlPullParser parser;

    public MyXmlParser() {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<SkiItem> parse(String xml) {

        ArrayList<SkiItem> resultList = new ArrayList();
        SkiItem dbo = null;

        TagType tagType = TagType.NONE;

        try {
            parser.setInput(new StringReader(xml));

//            태그 유형 구분 변수 준비
            int eventType = parser.getEventType();

//            parsing 수행 - for 문 또는 while 문으로 구성
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {    // 새로운 항목을 표현하는 태그를 만났을 경우 dto 객체 생성
                            dbo = new SkiItem();
                        } else if (parser.getName().equals(ADDRESS_TAG)) {
                            tagType = TagType.ADDRESS;
                        } else if (parser.getName().equals(TITLE_TAG)) {
                            tagType = TagType.TITLE;
                        }
                        else if (parser.getName().equals(TEL_TAG)) {
                            tagType = TagType.TEL;
                        }
                        else if (parser.getName().equals(MAPX_TAG)) {
                            tagType = TagType.MAPX;
                        }
                        else if (parser.getName().equals(MAPY_TAG)) {
                            tagType = TagType.MAPY;
                        }
                        else if (parser.getName().equals(CONTENTID_TAG)) {
                            tagType = TagType.CONTENTID;
                        }
                        else if (parser.getName().equals(OVERVIEW_TAG)) {
                            tagType = TagType.OVERVIEW;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dbo);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {       // 태그의 유형에 따라 dto 에 값 저장
                            case ADDRESS:
                                dbo.setAddress(parser.getText());
                                break;
                            case TITLE:
                                dbo.setTitle(parser.getText());
                                break;
                            case TEL:
                                dbo.setTel(parser.getText());
                                break;
                            case MAPX:
                                dbo.setMapX(parser.getText());
                                break;
                            case MAPY:
                                dbo.setMapY(parser.getText());
                                break;
                            case OVERVIEW:
                                dbo.setOverview(parser.getText());
                                break;
                            case CONTENTID:
                                dbo.setContentid(parser.getText());
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
