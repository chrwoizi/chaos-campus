namespace Javy
{
    partial class MapForm
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

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            EditorObjects.Map map1 = new EditorObjects.Map();
            this.mapControl1 = new Javy.MapControl();
            this.SuspendLayout();
            // 
            // mapControl1
            // 
            this.mapControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.mapControl1.Location = new System.Drawing.Point(0, 0);
            map1.Author = "";
            map1.Height = 0;
            map1.Version = 0;
            map1.Width = 0;
            this.mapControl1.Map = map1;
            this.mapControl1.Name = "mapControl1";
            this.mapControl1.ShowCollisions = true;
            this.mapControl1.ShowGrid = false;
            this.mapControl1.Size = new System.Drawing.Size(465, 291);
            this.mapControl1.TabIndex = 0;
            // 
            // MapForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(465, 291);
            this.Controls.Add(this.mapControl1);
            this.Name = "MapForm";
            this.TabText = "./javy - Das Handygame: Leveleditor";
            this.Text = "./javy - Das Handygame: Leveleditor";
            this.ResumeLayout(false);

        }

        #endregion

        private MapControl mapControl1;



    }
}

