package chianghao.core.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSHelper {

	private Workbook wb = null;
	
	public Workbook getWb() {
		return wb;
	}
	public void setWb(Workbook wb) {
		this.wb = wb;
	}
	public XLSHelper(String filePath) {
		wb = readExcel(filePath);
	}

	/**
	 * 获取sheets
	 * @return
	 */
	public Iterator<Sheet> getSheets() {
		 return wb.sheetIterator();
	}
	
	/**
	 * 获取sheet数据
	 * @param sheet   sheet
	 * @param index   取数据的开始行号
	 * @return
	 */
	public List<String[]> getSheetData(Sheet sheet,int index){
		List<String[]> datas = new ArrayList<String[]>();
		for(int i=index;i<sheet.getPhysicalNumberOfRows();i++) {
			Row row = sheet.getRow(i);
			if(row==null) {
				continue;
			}
			String[] data = new String[row.getPhysicalNumberOfCells()];
			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				data[j] = (String) getCellFormatValue(row.getCell(j));
			}
			datas.add(data);
		}
		return datas;
	}
	
	
	/** 
	 * 读取excel
	 * @param filePath
	 * @return
	 */
	private Workbook readExcel(String filePath) {
		if (filePath == null) {
			return null;
		}
		String extString = filePath.substring(filePath.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			if (".xls".equals(extString)) {
				return new HSSFWorkbook(is);
			} else if (".xlsx".equals(extString)) {
				return new XSSFWorkbook(is);
			} else {
				return null;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取单列的一个值
	 * @param cell
	 * @return
	 */
	public Object getCellFormatValue(Cell cell) {
		Object cellValue = "";
		if (cell != null) {
			switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				cellValue = String.valueOf(cell.getNumericCellValue());
				break;
			case STRING:
				cellValue = cell.getRichStringCellValue().getString();
				break;
			case FORMULA:
				if (DateUtil.isCellDateFormatted(cell)) {
					// 转换为日期格式YYYY-mm-dd
					cellValue = cell.getDateCellValue();
				} else {
					// 数字
					cellValue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			default:
				cellValue = "";
			}
		}
		return cellValue;
	}

}
