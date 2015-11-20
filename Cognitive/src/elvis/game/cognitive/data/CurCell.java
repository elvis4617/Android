package elvis.game.cognitive.data;


public class CurCell {
	private int row;
    private int col;
    private String content;
    
    
	public CurCell(int row, int col, String content) {
		super();
		this.row = row;
		this.col = col;
		this.content = content;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "CurCell [row=" + row + ", col=" + col + ", content=" + content
				+ "]";
	}
    
    
}
