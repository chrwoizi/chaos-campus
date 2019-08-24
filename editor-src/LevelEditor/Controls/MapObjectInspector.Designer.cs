namespace Javy.Controls
{
    partial class MapObjectInspector
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            EditorObjects.Map map1 = new EditorObjects.Map();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.Inspector = new System.Windows.Forms.PropertyGrid();
            this.mapControl1 = new Javy.MapControl();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.SuspendLayout();
            // 
            // splitContainer1
            // 
            this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer1.Location = new System.Drawing.Point(0, 0);
            this.splitContainer1.Name = "splitContainer1";
            this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.Controls.Add(this.Inspector);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.mapControl1);
            this.splitContainer1.Size = new System.Drawing.Size(157, 282);
            this.splitContainer1.SplitterDistance = 141;
            this.splitContainer1.TabIndex = 2;
            // 
            // Inspector
            // 
            this.Inspector.Dock = System.Windows.Forms.DockStyle.Fill;
            this.Inspector.Location = new System.Drawing.Point(0, 0);
            this.Inspector.Name = "Inspector";
            this.Inspector.PropertySort = System.Windows.Forms.PropertySort.Alphabetical;
            this.Inspector.Size = new System.Drawing.Size(157, 141);
            this.Inspector.TabIndex = 1;
            this.Inspector.ToolbarVisible = false;
            this.Inspector.PropertyValueChanged += new System.Windows.Forms.PropertyValueChangedEventHandler(this.Inspector_PropertyValueChanged);
            // 
            // mapControl1
            // 
            this.mapControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.mapControl1.Location = new System.Drawing.Point(0, 0);
            this.mapControl1.Name = "mapControl1";
            this.mapControl1.Size = new System.Drawing.Size(157, 137);
            this.mapControl1.TabIndex = 2;
            // 
            // MapObjectInspector
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.splitContainer1);
            this.Name = "MapObjectInspector";
            this.Size = new System.Drawing.Size(157, 282);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.PropertyGrid Inspector;
        private MapControl mapControl1;


    }
}
