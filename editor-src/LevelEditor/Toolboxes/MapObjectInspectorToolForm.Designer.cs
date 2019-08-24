namespace Javy
{
    partial class MapObjectInspectorToolForm
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
            this.panel1 = new System.Windows.Forms.Panel();
            this.mapObjectInspector1 = new Javy.Controls.MapObjectInspector();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.mapObjectInspector1);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(202, 383);
            this.panel1.TabIndex = 3;
            // 
            // mapObjectInspector1
            // 
            this.mapObjectInspector1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.mapObjectInspector1.Location = new System.Drawing.Point(0, 0);
            this.mapObjectInspector1.Name = "mapObjectInspector1";
            this.mapObjectInspector1.SelectedObject = null;
            this.mapObjectInspector1.Size = new System.Drawing.Size(202, 383);
            this.mapObjectInspector1.TabIndex = 1;
            // 
            // MapObjectInspectorToolForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(202, 383);
            this.Controls.Add(this.panel1);
            this.Name = "MapObjectInspectorToolForm";
            this.TabText = "Object Inspector";
            this.Text = "Object Inspector";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MapObjectInspectorToolForm_FormClosing);
            this.panel1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel panel1;
        private Javy.Controls.MapObjectInspector mapObjectInspector1;

    }
}