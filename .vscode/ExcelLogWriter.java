
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * jxlパッケージ（外部ライブラリの）を利用してエクセルファイルを操作するための基本操作を定義したクラス．<br>
 *
 * @author miyazakiという名の松本＋後藤さん
 * @verion 2008/12/20
 *
 * ExcelLogWriterAdapterで使用するメソッドが書かれている
 */

public class ExcelLogWriter {

	//
	private WorkbookSettings settings;

	/**
	 * ExcelLogWriterのコンストラクタ
	 */
	ExcelLogWriter() {

		settings = new WorkbookSettings();
		settings.setGCDisabled(true);
	}

	//書き込んだワークブックを閉じて、保存するメソッド⇒この作業で出力できる
	public WritableWorkbook closeBook(WritableWorkbook wb) {

		try {

			wb.close();

		} catch (WriteException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return wb;
	}

	//ワークブックの名前を決めて、出力形式も決めるメソッド
	public WritableWorkbook generateBook(String bookName) {

		WritableWorkbook wb = null;

		try {
			wb = Workbook.createWorkbook(new File(bookName + ".xls"), settings);

		} catch (IOException e1) {

			e1.printStackTrace();

		}
		return wb;
	}

	//ワークブックにシートを作成するメソッド
	public WritableSheet generateSheet(WritableWorkbook wb, String sheetName, int sheetNum) {

		WritableSheet ws = null;
//		System.out.println("ws"+ws);
//		System.out.println("wb"+wb);
		ws = wb.createSheet(sheetName, sheetNum);

		return ws;

	}

	//Arraylist形式（中身がdouble型）のデータをシートに書き込むメソッド
	public void setDoubleLabels(ArrayList<Double> labels, WritableSheet ws, int linePos, int colPos) {

		for (int j = colPos; j < colPos + labels.size(); j++) {

			Number num = new Number(j, linePos, labels.get(j - colPos));

			try {

				ws.addCell(num);

			} catch (RowsExceededException e) {

				e.printStackTrace();

			} catch (WriteException e) {
				e.printStackTrace();
			}

		}

	}


	//配列形式のデータをシートに書き込むメソッド
	public void setDoubleMatrix(double []labels, WritableSheet ws, int linePos, int colPos) {

		for (int j = colPos; j < colPos + labels.length; j++) {

			Number num = new Number(j, linePos, labels[j - colPos]);

			try {

				ws.addCell(num);

			} catch (RowsExceededException e) {

				e.printStackTrace();

			} catch (WriteException e) {

				e.printStackTrace();

			}

		}
	}
	//Arraylist形式（中身がint型）のデータをシートに書き込むメソッド
	public void setIntLabels(ArrayList<Integer> labels, WritableSheet ws, int linePos, int colPos) {

		for (int j = linePos; j < linePos + labels.size(); j++) {

			Number num = new Number(colPos, j, labels.get(j - linePos));

			try {

				ws.addCell(num);

			} catch (RowsExceededException e) {

				e.printStackTrace();

			} catch (WriteException e) {

				e.printStackTrace();

			}

		}

	}
	//配列形式（中身がdouble型）のデータをシートに書き込むメソッド
	public void setLine(double line[], WritableSheet ws, int linePos, int colPos) {

		for (int i = linePos; i < linePos + line.length; i++) {

			Number num = new Number(colPos, i, line[i - linePos]);

			try {

				ws.addCell(num);

			} catch (RowsExceededException e) {

				e.printStackTrace();

			} catch (WriteException e) {

				e.printStackTrace();

			}

		}
	}
	//配列形式（中身がint型）のデータをシートに書き込むメソッド
	public void setLine(int line[], WritableSheet ws, int linePos, int colPos) {

		for (int i = linePos; i < linePos + line.length; i++) {

			Number num = new Number(colPos, i, line[i - linePos]);

			try {

				ws.addCell(num);

			} catch (RowsExceededException e) {

				e.printStackTrace();

			} catch (WriteException e) {

				e.printStackTrace();

			}

		}

	}
	//二元配列形式（中身がdouble型）のデータをシートに書き込むメソッド
	public void setMatrix(double matrix[][], WritableSheet ws, int linePos, int colPos) {

		for (int i = linePos; i < linePos + matrix.length; i++) {

			for (int j = colPos; j < colPos + matrix[i - linePos].length; j++) {

				Number num = new Number(j, i, matrix[i - linePos][j - colPos]);

				try {

					ws.addCell(num);

				} catch (RowsExceededException e) {

					e.printStackTrace();

				} catch (WriteException e) {

					e.printStackTrace();

				}

			}

		}

	}

	//二元配列形式（中身がint型）のデータをシートに書き込むメソッド
	public void setMatrix(int matrix[][], WritableSheet ws, int linePos, int colPos) {

		for (int i = linePos; i < linePos + matrix.length; i++) {

			for (int j = colPos; j < colPos + matrix[i - linePos].length; j++) {

				Number num = new Number(j, i, matrix[i - linePos][j - colPos]);

				try {

					ws.addCell(num);

				} catch (RowsExceededException e) {

					e.printStackTrace();

				} catch (WriteException e) {

					e.printStackTrace();

				}

			}

		}

	}

	//二元配列形式（中身がString型）のデータをシートに書き込むメソッド
	public void setMatrix(String matrix[][], WritableSheet ws, int linePos, int colPos) {

//		System.out.println(matrix[0].length);

		for (int i = linePos; i < linePos + matrix.length; i++) {

			for (int j = colPos; j < colPos + matrix[i - linePos].length; j++) {


				Label label = new Label(j, i, matrix[i - linePos][j - colPos]);

				try {
					//System.out.println(matrix[i - linePos][j - colPos]);
					ws.addCell(label);

				} catch (RowsExceededException e) {

					e.printStackTrace();

				} catch (WriteException e) {

					e.printStackTrace();

				}

			}

		}

	}

	//配列形式（中身がString型）のデータをシートに書き込むメソッド
	public void setStringArrayList2Matrix (ArrayList <String> stringList, WritableSheet sheet, int startLinePosition,
			 int startColumnPosition) {

		String [][] matrix = new String [stringList.size ()][1];

		for (int i = 0; i < stringList.size (); i++) {

			matrix[i][0] = stringList.get (i);

		}

		setMatrix (matrix, sheet, startLinePosition, startColumnPosition);

	}


	//配列形式（中身がString型）のデータをシートに書き込むメソッド
	public void setStrLabels(ArrayList<String> labels, WritableSheet sheet, int linePos, int colPos) {

		for (int j = colPos; j < colPos + labels.size(); j++) {

			Label label = new Label(j, linePos, labels.get(j - colPos));

			try {

				sheet.addCell(label);
				} catch (RowsExceededException e) {

				e.printStackTrace();

			} catch (WriteException e) {

				e.printStackTrace();

			}

		}

	}
	//書き込んだワークブックに今までの内容を書き込むメソッド
	public WritableWorkbook writeBook(WritableWorkbook wb) {

		
		try {
			wb.write();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return wb;
	}

}
